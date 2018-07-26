package com.example.fileservice.controllers;

import com.example.fileservice.FileRepository;
import com.example.fileservice.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

@RestController
public class FilesController {

    @Autowired
    FilesService filesService;
    @Autowired
    FileRepository fileRepository;

    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public void loadFileToServer(@RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "id", required = false) Long userId) {
        filesService.uploadUserFile(file, userId);
    }

    @RequestMapping(value = "/files/{id}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable long id) throws FileNotFoundException {
        return filesService.downloadFile(id);
    }

    @RequestMapping(value = "/files/{id}", method = RequestMethod.DELETE)
    public void deleteFileByServer(@PathVariable long id) {
        filesService.deleteUserFile(id);
    }


}
