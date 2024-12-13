package com.dev.identity_service.exception;

import com.dev.identity_service.enums.ErrorCode;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    // Constructor accepting ErrorCode enum
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
