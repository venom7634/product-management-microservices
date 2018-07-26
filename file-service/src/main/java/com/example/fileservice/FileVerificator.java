package com.example.fileservice;


import com.example.fileservice.entity.UserFile;
import com.example.fileservice.exception.IdenticalFilesException;
import com.example.fileservice.exception.InvalidTypeException;
import com.example.fileservice.exception.MaxAmountSizeException;
import com.example.fileservice.exception.NoAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Component
public class FileVerificator {

    @Value("${files.user-file.type-files}")
    private String[] authorizedTypes;


    public void checkToMaxAmountSizeFiles(MultipartFile file, List<UserFile> files, long max) {
        long amount = 0;

        for (UserFile userFile : files) {
            amount += userFile.getSize();
        }

        if ((amount + file.getSize()) > max) {
            throw new MaxAmountSizeException();
        }
    }

    public boolean directoryIsExist(String path) {
        File directory = new File(path);
        return directory.exists();
    }

    public void checkToCorrectFile(MultipartFile file) {
        String typeFile = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
        if (!Arrays.asList(authorizedTypes).contains(typeFile)) {
            throw new InvalidTypeException();
        }
    }

    public void checkIdenticalFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            throw new IdenticalFilesException();
        }
    }

    public void checkAccessToFile(UserFile file, long userId) {
        if (file.getUserId() != userId) {
            throw new NoAccessException();
        }
    }
}
