package com.example.fileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "FileUser is damaged")
public class FileDamagedException extends RuntimeException {
}
