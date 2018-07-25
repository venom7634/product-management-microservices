package com.example.fileservice;

import com.example.fileservice.entity.Token;
import com.example.fileservice.dto.UserFileRequest;
import com.example.fileservice.entity.UserFile;
import com.example.fileservice.exception.FileDamagedException;
import com.example.fileservice.exception.FileEmptyException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;

@Service
public class FilesService {

    @Resource(name = "token")
    Token token;

    @Autowired
    FileVerificator fileVerificator;

    private final FileRepository fileRepository;

    @Autowired
    public FilesService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void uploadFile(MultipartFile file) {
        if (!file.isEmpty()) {
            long userId = getIdByToken(token.getToken());
            String path = "Files-Service/user_" + userId + "/";
            if (!fileVerificator.direcotryIsExist(path)) {
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
                fileRepository.addFileInDataBase(userId, file.getOriginalFilename());
            } catch (Exception e) {
                throw new FileDamagedException();
            }
        } else {
            throw new FileEmptyException();
        }
    }

    public long getLastIdFileByUser() {
        return fileRepository.getLastIdUserFile(getIdByToken(token.getToken()));
    }

    public void deleteUserFile(long id) {
        UserFile file = fileRepository.getFileById(id);

        String path = "Files-Service/user_" + getIdByToken(token.getToken()) + "/" + file.getName();
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
}
