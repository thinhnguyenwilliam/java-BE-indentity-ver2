package com.dev.identity_service.dto.request;

import com.dev.identity_service.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest
{
    @Size(min = 3, max = 20, message = "USERNAME_INVALID") // key of enums
    String username;

    @Size(min = 5, max = 20, message = "PASSWORD_INVALID")
    String password;

    String firstName;
    String lastName;


    @DobConstraint(min = 2, message = "INVALID_DOB")
    LocalDate dob;
}
