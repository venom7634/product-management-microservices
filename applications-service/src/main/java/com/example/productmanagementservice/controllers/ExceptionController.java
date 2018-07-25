package com.example.productmanagementservice.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity expiredToken(RuntimeException ex, WebRequest request) {
        logger.error("Token expired");
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SignatureException.class)
    protected ResponseEntity incorretValue(RuntimeException ex, WebRequest request) {
        logger.error("Token not valid");
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }
}
