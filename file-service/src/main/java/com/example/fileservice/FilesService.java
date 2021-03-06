package com.example.fileservice;

import com.example.fileservice.clients.UsersServiceClient;
import com.example.fileservice.entity.Access;
import com.example.fileservice.entity.FileUser;
import com.example.fileservice.entity.Token;
import com.example.fileservice.entity.User;
import com.example.fileservice.exception.FileDamagedException;
import com.example.fileservice.exception.FileEmptyException;
import com.example.fileservice.exception.IncorrectAccessibilityValueException;
import com.example.fileservice.exception.NoAccessException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;

@Service
public class FilesService {

    @Value("${files.user-file.max-amount-size}")
    private String maxAmountSizeFiles;

    @Resource(name = "token")
    Token token;

    @Autowired
    UsersServiceClient usersServiceClient;

    @Autowired
    FileVerificator fileVerificator;

    private final FileRepository fileRepository;

    @Autowired
    public FilesService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }


    private long getMaxAmountSizeFiles() {
        switch (maxAmountSizeFiles.substring(maxAmountSizeFiles.length() - 2)) {
            case "KB":
                return Long.valueOf(maxAmountSizeFiles.substring(0, maxAmountSizeFiles.length() - 2)) * 1024;
            case "MB":
                return Long.valueOf(maxAmountSizeFiles.substring(0, maxAmountSizeFiles.length() - 2)) * 1024 * 1024;
            case "GB":
                return Long.valueOf(maxAmountSizeFiles.substring(0, maxAmountSizeFiles.length() - 2)) * 1024 * 1024 * 1024;
            default:
                return Long.valueOf(maxAmountSizeFiles);
        }
    }

    public void uploadUserFile(MultipartFile file, long[] usersId, Integer accessibility) {
        fileVerificator.checkToCorrectFile(file);

        if (accessibility != 0 && accessibility != 1 && usersId == null) {
            throw new IncorrectAccessibilityValueException();
        }
        User user = takeUserForToken();

        fileVerificator.checkToMaxAmountSizeFiles
                (file, fileRepository.getAllUserFiles(user.getId()), getMaxAmountSizeFiles());

        if (user.getSecurity() == User.access.EMPLOYEE_BANK.getNumber()) {
            uploadUserFileForManager(file, accessibility, usersId, user);
        } else {
            uploadUserFileForUser(file, user.getId());
        }
    }

    private void uploadUserFileForUser(MultipartFile file, long userId) {
        uploadFile(file, userId, FileUser.accessibility.CLOSED.getAccess());
    }

    private User takeUserForToken() {
        return usersServiceClient.getUserById(getIdByToken(token.getToken()));
    }

    private void uploadUserFileForManager(MultipartFile file, int accessibility, long[] usersId, User user) {
        uploadFile(file, user.getId(), accessibility);

        if (usersId != null) {
            FileUser fileUser = fileRepository.getLastCreatedFile(user.getId());
            for (long id : usersId) {
                fileRepository.addAccessToFileForUser(user.getId(), id, fileUser.getId());
            }
        }
    }

    private void uploadFile(MultipartFile file, long userId, int accessibility) {
        if (!file.isEmpty()) {
            String path = "Files-Service/user_" + userId + "/";
            if (!fileVerificator.directoryIsExist(path)) {
                createNewDirectory(path);
            }
            fileVerificator.checkIdenticalFiles(path + file.getOriginalFilename());
            uploadFile(file, path);
            fileRepository.addFileInDataBase(userId, file.getOriginalFilename(), file.getSize(), accessibility);
        } else {
            throw new FileEmptyException();
        }
    }

    private void uploadFile(MultipartFile file, String path) {

        try {
            File newFile = new File(path + file.getOriginalFilename());
            newFile.createNewFile();

            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(newFile));
            stream.write(file.getBytes());
            stream.close();

        } catch (Exception e) {
            throw new FileDamagedException();
        }

    }

    public ResponseEntity<InputStreamResource> downloadFile(long id) throws FileNotFoundException {
        User user = takeUserForToken();
        FileUser file = fileRepository.getFileById(id);

        if (file.getAccessibility() == 0) {
            checkAccessToFile(file, user);
        }

        InputStreamResource body = createInputStreamFromFile(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .body(body);
    }

    private void checkAccessToFile(FileUser file, User user) {
        User userFile = usersServiceClient.getUserById(file.getUserId());
        List<Access> accesses = fileRepository.getAllAccessForUser(user.getId());

        if (userFile.getSecurity() != User.access.EMPLOYEE_BANK.getNumber() && !accessIsExists(accesses, file.getId())) {
            fileVerificator.checkAccessToFile(file, user.getId());
        }
    }

    private boolean accessIsExists(List<Access> accesses, long fileId) {
        for (Access access : accesses) {
            if (access.getFileId() == fileId) {
                return true;
            }
        }
        return false;
    }

    public void deleteUserFile(long id) {
        User user = takeUserForToken();
        FileUser file = fileRepository.getFileById(id);
        User userFile = usersServiceClient.getUserById(file.getUserId());

        String path = createPath(file);
        File deletedFile = new File(path);

        if (user.getSecurity() == User.access.EMPLOYEE_BANK.getNumber()) {
            deleteForManager(deletedFile, userFile, user, id);
        } else {
            deleteForUser(deletedFile, userFile, user, id);
        }
    }

    private void deleteForManager(File deletedFile, User userFile, User user, long id) {
        if (userFile.getSecurity() == User.access.EMPLOYEE_BANK.getNumber() && userFile.getId() != user.getId()) {
            throw new NoAccessException();
        } else {
            deleteFile(deletedFile, id);
        }
    }

    private void deleteFile(File deletedFile, long id) {
        deletedFile.delete();
        fileRepository.deleteFile(id);
        fileRepository.deleteAllAccessToFile(id);
    }

    private void deleteForUser(File deletedFile, User userFile, User user, long id) {
        if (userFile.getId() == user.getId()) {
            deleteFile(deletedFile, id);
        } else {
            throw new NoAccessException();
        }
    }

    private String createPath(FileUser file) {
        return "Files-Service/user_" + file.getUserId() + "/" + file.getName();
    }

    private InputStreamResource createInputStreamFromFile(FileUser file) throws FileNotFoundException {
        String path = createPath(file);
        File responseFile = new File(path);

        return new InputStreamResource(new FileInputStream(responseFile));
    }

    private void createNewDirectory(String path) {
        File directory = new File(path);
        directory.mkdirs();
    }

    private long getIdByToken(String token) {
        int i = token.lastIndexOf('.');
        String tokenWithoutKey = token.substring(0, i + 1);
        if (tokenWithoutKey.equals("")) {
            throw new SignatureException("Signature token not valid");
        }
        return Long.parseLong(Jwts.parser().parseClaimsJwt(tokenWithoutKey).getBody().getSubject());
    }

    private boolean authenticationOfBankEmployee(int securityStatus) {
        if (securityStatus == User.access.EMPLOYEE_BANK.getNumber()) {
            return true;
        }
        return false;
    }

}
