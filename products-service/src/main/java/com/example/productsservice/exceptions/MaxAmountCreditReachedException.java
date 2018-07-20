package com.example.productsservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Maximum credit amount reached")
public class MaxAmountCreditReachedException extends RuntimeException {
}
