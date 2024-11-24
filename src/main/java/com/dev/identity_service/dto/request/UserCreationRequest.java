package com.dev.identity_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;


import java.time.LocalDate;

@Data
public class UserCreationRequest
{
    @Size(min = 3, max = 20, message = "USERNAME_INVALID") // key of enums
    String username;

    @Size(min = 5, max = 20, message = "PASSWORD_INVALID")
    String password;

    String firstName;
    String lastName;
    LocalDate dob;
}
