package com.example.detector.exception;

public class InvalidDnaException extends RuntimeException {

    public InvalidDnaException(String message) {
        super(String.format("Invalid DNA input: %s", message));
    }
}
