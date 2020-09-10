package com.example.detector.interceptor;

import com.example.detector.exception.InvalidDnaException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({InvalidDnaException.class})
    public ResponseEntity<String> handleInvalidDnaException(final InvalidDnaException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
