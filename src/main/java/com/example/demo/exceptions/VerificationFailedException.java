package com.example.demo.exceptions;

public class VerificationFailedException extends RuntimeException {
    public VerificationFailedException(String message) {
        super(message);
    }
}
