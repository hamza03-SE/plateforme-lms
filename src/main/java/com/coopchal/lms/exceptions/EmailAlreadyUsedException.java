package com.coopchal.lms.exceptions;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
