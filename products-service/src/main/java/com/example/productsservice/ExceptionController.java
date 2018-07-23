package com.example.productsservice;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity handleConflict(RuntimeException ex, WebRequest request){
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

}
