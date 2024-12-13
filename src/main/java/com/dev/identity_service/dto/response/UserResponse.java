package com.dev.identity_service.dto.response;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    // Removed password from the response for security reasons
    String firstName;
    String lastName;
    String fullName;

    // @JsonFormat(pattern = "dd-MM-yyy")
    LocalDate dob;

    Set<RoleResponse> roles;
}
