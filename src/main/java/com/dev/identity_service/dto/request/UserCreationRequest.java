package com.dev.identity_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;


import java.time.LocalDate;

@Data
public class UserCreationRequest
{
    @Size(min = 2, max = 20, message = "username is short man oh man")
    String username;

    @Size(min = 5, max = 20, message = "password is short man oh man")
    String password;

    String firstName;
    String lastName;
    LocalDate dob;
}
