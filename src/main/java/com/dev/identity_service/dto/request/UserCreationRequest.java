package com.dev.identity_service.dto.request;

import lombok.Data;


import java.time.LocalDate;

@Data
public class UserCreationRequest
{
    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate dob;
}
