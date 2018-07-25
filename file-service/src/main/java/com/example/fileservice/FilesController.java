package com.example.fileservice;

import com.example.fileservice.dto.UserFileResponse;
import com.example.fileservice.entity.UserFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
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
    public UserFileResponse loadFileToServer(@RequestParam("file") MultipartFile file){
        filesService.uploadFile(file);
        UserFileResponse response = new UserFileResponse();
        response.setId(filesService.getLastIdFileByUser());
        return response;
    }

    @RequestMapping(value = "/files/{id}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable long id) throws FileNotFoundException {
        UserFile file = fileRepository.getFileById(id);
        InputStreamResource body = filesService.createInputStreamFromFile(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .body(body);
    }

    @RequestMapping(value = "/files/{id}", method = RequestMethod.DELETE)
    public void deleteFileByServer(@PathVariable long id){
        filesService.deleteUserFile(id);
    }


}
