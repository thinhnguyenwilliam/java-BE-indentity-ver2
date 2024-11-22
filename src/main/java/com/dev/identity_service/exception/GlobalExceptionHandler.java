package com.dev.identity_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler
{

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex)
    {
        // Log the exception details
        logger.error("RuntimeException occurred: ", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidException(MethodArgumentNotValidException ex)
    {
        // Log the exception details
        logger.error("MethodArgumentNotValidException occurred: ", ex);
        return ResponseEntity.badRequest().body(Objects.requireNonNull(ex.getFieldError()).getDefaultMessage());
    }




}
