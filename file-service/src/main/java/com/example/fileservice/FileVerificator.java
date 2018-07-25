package com.example.fileservice;


import com.example.fileservice.entity.UserFile;
import com.example.fileservice.exception.IdenticalFilesException;
import com.example.fileservice.exception.NoAccessException;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileVerificator {

    public boolean directoryIsExist(String path){
        File directory = new File(path);
        return directory.exists();
    }

    public void checkIdenticalFiles(String path){
        File file = new File(path);
        if(file.exists()){
            throw new IdenticalFilesException();
        }
    }

    public void checkAccessToFile(UserFile file, long userId){
        if(file.getUserId() != userId){
            throw new NoAccessException();
        }
    }
}
