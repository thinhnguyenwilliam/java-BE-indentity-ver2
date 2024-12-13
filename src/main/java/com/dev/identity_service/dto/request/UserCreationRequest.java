package com.dev.identity_service.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import com.dev.identity_service.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, max = 20, message = "USERNAME_INVALID") // key of enums
    String username;

    @Size(min = 5, max = 20, message = "PASSWORD_INVALID")
    String password;

    String firstName;
    String lastName;

    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dob;
}
