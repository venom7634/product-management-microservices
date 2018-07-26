package com.example.fileservice.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity expiredToken(RuntimeException ex, WebRequest request) {
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SignatureException.class)
    protected ResponseEntity incorretValue(RuntimeException ex, WebRequest request) {
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity maxSizeFile(RuntimeException ex, WebRequest request) {
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity nullValue(RuntimeException ex, WebRequest request) {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
