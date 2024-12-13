package com.dev.identity_service.exception;

import java.util.*;

import jakarta.validation.ConstraintViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.dev.identity_service.dto.response.ApiResponse;
import com.dev.identity_service.enums.ErrorCode;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(RuntimeException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        // Log the exception details
        // logger.error("RuntimeException occurred: ", ex);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        // Build the ApiResponse object
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        // Log the exception details
        logger.error("AppException occurred: Code = {}, Message = {}", errorCode.getCode(), errorCode.getMessage(), ex);

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    private String mapAttribute(String message, Map<?, ?> attributes) {
        //        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        //
        //        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);

        // code can't test???
        // Iterate over all keys in the attributes map
        for (Map.Entry<?, ?> entry : attributes.entrySet()) {
            // Only replace placeholders in the message that match the key
            if (entry.getKey() instanceof String key) {
                Object value = entry.getValue();

                // Replace {key} with the corresponding value from attributes
                String placeholder = "{" + key + "}";
                if (message.contains(placeholder))
                    message = message.replace(placeholder, value != null ? value.toString() : "");
            }
        }
        return message;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidException(MethodArgumentNotValidException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        ErrorCode errorCode = ErrorCode.INVALID_ENUM_KEY;

        // List to collect all attributes
        List<Map<?, ?>> allAttributes = new ArrayList<>();

        try {
            // Extract the first error message (enumKey)
            String enumKey = ex.getBindingResult().getFieldErrors().stream()
                    .findFirst()
                    .map(FieldError::getDefaultMessage)
                    .orElse("INVALID_ENUM_KEY");

            errorCode = ErrorCode.valueOf(enumKey); // Attempt to map the enumKey to an ErrorCode

            // Retrieve details about the violated constraint
            ex.getBindingResult().getFieldErrors().stream().findFirst().ifPresent(fieldError -> {
                var constraintDescriptor =
                        fieldError.unwrap(ConstraintViolation.class).getConstraintDescriptor();
                Map<?, ?> attributes = constraintDescriptor.getAttributes();

                // Log the `min` value from the attributes
                if (attributes.containsKey("min")) logger.info("Constraint 'min' value: {}", attributes.get("min"));
                else logger.info("No 'min' attribute found for the constraint.");

                // Store the attributes for later use
                allAttributes.add(attributes);
            });

        } catch (IllegalArgumentException e) {
            logger.error("Invalid enum key: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected exception occurred: ", e);
        }

        // Now that the loop is finished, log all collected attributes
        logger.info("Collected constraint attributes: {}", allAttributes);

        // Replace the placeholder {min} in the error message
        String mappedMessage = mapAttribute(errorCode.getMessage(), allAttributes.getFirst());

        // Set error details in the response
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(mappedMessage);

        // Log the exception details
        logger.error("Validation exception: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        // Build the ApiResponse object
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        // Log the access denied exception
        logger.warn("Access denied: {}", ex.getMessage());

        // Return the response with 403 Forbidden status
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }
}
