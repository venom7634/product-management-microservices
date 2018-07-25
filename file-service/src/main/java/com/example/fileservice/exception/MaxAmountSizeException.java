package com.example.fileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Reach the maximum allowed size")
public class MaxAmountSizeException extends RuntimeException {
}
