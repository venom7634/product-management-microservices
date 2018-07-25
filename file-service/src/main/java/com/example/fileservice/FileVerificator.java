package com.example.fileservice;


import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileVerificator {

    public boolean direcotryIsExist(String path){
        File directory = new File(path);
        return directory.exists();
    }
}
