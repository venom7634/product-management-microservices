package com.example.fileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Incorrect accessibility parameter. It can only 0 or 1")
public class IncorrectAccessibilityValueException extends RuntimeException {
}
