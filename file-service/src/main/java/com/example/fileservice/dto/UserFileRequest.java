package com.example.fileservice.dto;

import org.springframework.web.multipart.MultipartFile;

public class UserFileRequest {

    MultipartFile file;
    String name;

    public UserFileRequest(){

    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
