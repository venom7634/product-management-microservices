package com.example.fileservice;

import com.example.fileservice.clients.UsersServiceClient;
import com.example.fileservice.entity.Token;
import com.example.fileservice.entity.User;
import com.example.fileservice.entity.UserFile;
import com.example.fileservice.exception.FileDamagedException;
import com.example.fileservice.exception.FileEmptyException;
import com.example.fileservice.exception.InvalidTypeException;
import com.example.fileservice.exception.NoAccessException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.Arrays;

@Service
public class FilesService {

    @Value("${files.user-file.type-files}")
    private String[] authorizedTypes;

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

    private void checkToCorrectTypeFile(MultipartFile file) {
        String typeFile = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
        if(!Arrays.asList(authorizedTypes).contains(typeFile)){
            throw new InvalidTypeException();
        }
    }

    public void uploadUserFile(MultipartFile file, long userId) {
        checkToCorrectTypeFile(file);

        if (userId == -1) {
            uploadUserFileForUser(file);
        } else {
            uploadUserFileForBank(file, userId);
        }
    }

    public void uploadUserFileForUser(MultipartFile file) {
        long userId = getIdByToken(token.getToken());
        uploadFile(file, userId);
    }

    public void uploadUserFileForBank(MultipartFile file, long userId) {
        User user = usersServiceClient.getUserById(getIdByToken(token.getToken()));
        if (!authenticationOfBankEmployee(user.getSecurity())) {
            throw new NoAccessException();
        }
        uploadFile(file, userId);
    }

    private void uploadFile(MultipartFile file, long userId) {
        if (!file.isEmpty()) {
            String path = "Files-Service/user_" + userId + "/";
            fileVerificator.checkIdenticalFiles(path + file.getOriginalFilename());
            uploadFile(file, path);
            fileRepository.addFileInDataBase(userId, file.getOriginalFilename());
        } else {
            throw new FileEmptyException();
        }
    }

    private void uploadFile(MultipartFile file, String path) {
        if (!fileVerificator.directoryIsExist(path)) {
            createNewDirectory(path);
        }
        try {
            byte[] bytes = file.getBytes();
            File newFile = new File(path + file.getOriginalFilename());
            newFile.createNewFile();

            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(newFile));
            stream.write(bytes);
            stream.close();

        } catch (Exception e) {
            throw new FileDamagedException();
        }
    }

    public ResponseEntity<InputStreamResource> downloadFile(long id) throws FileNotFoundException {
        User user = usersServiceClient.getUserById(getIdByToken(token.getToken()));
        UserFile file = fileRepository.getFileById(id);

        checkAccessToFile(file, user);

        InputStreamResource body = createInputStreamFromFile(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .body(body);
    }

    private void checkAccessToFile(UserFile file, User user) {
        if (!authenticationOfBankEmployee(user.getSecurity())) {
            fileVerificator.checkAccessToFile(file, user.getId());
        }
    }

    public void deleteUserFile(long id) {
        User user = usersServiceClient.getUserById(getIdByToken(token.getToken()));
        UserFile file = fileRepository.getFileById(id);

        checkAccessToFile(file, user);

        String path = "Files-Service/user_" + file.getUserId() + "/" + file.getName();
        File deletedFile = new File(path);
        deletedFile.delete();
        fileRepository.deleteFile(id);
    }

    public InputStreamResource createInputStreamFromFile(UserFile file) throws FileNotFoundException {
        String path = "Files-Service/user_" + file.getUserId() + "/" + file.getName();
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
