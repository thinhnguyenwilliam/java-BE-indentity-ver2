package com.dev.identity_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode
{
    // General Errors
    INTERNAL_SERVER_ERROR(1000, "An unexpected error occurred"),
    VALIDATION_ERROR(1001, "Validation failed"),
    RESOURCE_NOT_FOUND(1002, "Resource not found"),
    UNAUTHORIZED_ACCESS(1003, "Unauthorized access"),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception"),
    INVALID_ENUM_KEY(9996, "(Uncategorized exception)--Key of enum data is invalid"),

    // User Errors
    USER_ALREADY_EXISTS(2000, "User already exists sad man"),
    USER_NOT_FOUND(2001, "User not found"),
    USERNAME_INVALID(9998, "Username must be a least 3 characters"),
    PASSWORD_INVALID(9997, "Password must be a least 5 characters"),


    // Authentication/Authorization Errors
    INVALID_CREDENTIALS(3000, "Invalid credentials"),
    TOKEN_EXPIRED(3001, "Token has expired"),
    ACCESS_DENIED(3002, "Access denied");

    private final int code;
    private final String message;

}
