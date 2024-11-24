package com.dev.identity_service.exception;

import com.dev.identity_service.dto.response.ApiResponse;
import com.dev.identity_service.enums.ErrorCode;
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

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(RuntimeException ex)
    {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());


        // Log the exception details
        logger.error("RuntimeException occurred: ", ex);
        return ResponseEntity.badRequest().body(apiResponse);
    }


    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex)
    {
        ErrorCode errorCode = ex.getErrorCode();

        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());


        // Log the exception details
        logger.error("AppException occurred: ", ex);
        return ResponseEntity.badRequest().body(apiResponse);
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidException(MethodArgumentNotValidException ex)
    {
        String enumKey= Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        //ErrorCode errorCode = ErrorCode.valueOf(enumKey);
        ErrorCode errorCode = ErrorCode.INVALID_ENUM_KEY;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
        }catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException occurred: ", e);
        }

        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());


        // Log the exception details
        logger.error("MethodArgumentNotValidException occurred: ", ex);
        return ResponseEntity.badRequest().body(apiResponse);
    }




}
