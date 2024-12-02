package com.dev.identity_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode
{
    // General Errors
    INTERNAL_SERVER_ERROR(1000, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(1001, "Validation failed", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND(1002, "Resource not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACCESS(1003, "Unauthorized access", HttpStatus.UNAUTHORIZED),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception sad man", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ENUM_KEY(9996, "(Uncategorized exception)--Key of enum data is invalid", HttpStatus.BAD_REQUEST),

    // User Errors
    USER_ALREADY_EXISTS(2000, "User already exists sad man", HttpStatus.CONFLICT),
    USER_NOT_FOUND(2001, "User not found", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(9998, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(9997, "Password must be at least 5 characters", HttpStatus.BAD_REQUEST),

    // Authentication/Authorization Errors
    INVALID_CREDENTIALS(3000, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(3001, "Token has expired", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(3002, "Access denied", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(3003, "You don't have permission", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(3004, "You don't have permission for unauthenticated", HttpStatus.UNAUTHORIZED)
    ;


    private final int code;
    private final String message;
    private final HttpStatus statusCode;
}
