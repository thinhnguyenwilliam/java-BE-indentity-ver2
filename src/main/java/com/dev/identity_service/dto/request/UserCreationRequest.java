package com.dev.identity_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class UserCreationRequest
{
    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate dob;
}
