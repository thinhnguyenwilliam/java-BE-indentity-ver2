package com.dev.identity_service.service;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.dev.identity_service.dto.request.UserCreationRequest;
import com.dev.identity_service.dto.response.UserResponse;
import com.dev.identity_service.entity.User;
import com.dev.identity_service.enums.ErrorCode;
import com.dev.identity_service.exception.AppException;
import com.dev.identity_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest
{
    @Autowired
    private UserService userService;

    @MockBean
    UserRepository userRepository;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private User mockUser;

    @BeforeEach
    void setUp()
    {
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);

        userCreationRequest = UserCreationRequest.builder()
                .username("JohnHanno")
                .firstName("John")
                .lastName("Hanno")
                .password("12334655")
                .dob(dateOfBirth)
                .build();

        userResponse = UserResponse.builder()
                .id("12cf2543b476765")
                .username("JohnHanno")
                .firstName("John")
                .lastName("Hanno")
                .dob(dateOfBirth)
                .build();

        mockUser=User.builder()
                .id("12cf2543b476765")
                .username("JohnHanno")
                .firstName("John")
                .lastName("Hanno")
                .dob(dateOfBirth)
                .build();
    }

    @Test
    void createUser_validRequest_success()
    {
        //GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(mockUser);

        // WHEN
        UserResponse actualResponse = userService.createUser(userCreationRequest);

        // THEN
        assertEquals(userResponse.getId(), actualResponse.getId(), "The IDs should match.");
        assertEquals(userResponse.getUsername(), actualResponse.getUsername(), "The usernames should match.");
        assertEquals(userResponse.getFirstName(), actualResponse.getFirstName(), "The first names should match.");
        assertEquals(userResponse.getLastName(), actualResponse.getLastName(), "The last names should match.");
        assertEquals(userResponse.getDob(), actualResponse.getDob(), "The date of birth should match.");

    }


    @Test
    void createUser_usernameAlreadyExists_throwsException() {
        // GIVEN
        when(userRepository.existsByUsername(userCreationRequest.getUsername())).thenReturn(true);

        // WHEN & THEN
        AppException exception = assertThrows(AppException.class, () ->
                userService.createUser(userCreationRequest)
        );
        assertEquals(ErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());

    }

    @Test
    @WithMockUser(username = "harry")
    void getMyInfo_valid_success() {
        // GIVEN
        when(userRepository.findByUsername("harry")).thenReturn(java.util.Optional.of(mockUser));


        // WHEN
        UserResponse actualResponse = userService.getMyInfo();

        // THEN
        assertEquals("JohnHanno", actualResponse.getUsername(), "The username should match.");
        assertEquals("12cf2543b476765", actualResponse.getId(), "The ID should match.");
    }

    @Test
    @WithMockUser(username = "harry")
    void getMyInfo_userNotFound_throwsException()
    {
        // GIVEN
        when(userRepository.findByUsername("harry")).thenReturn(java.util.Optional.empty());


        // WHEN & THEN
        AppException exception = assertThrows(AppException.class, () -> userService.getMyInfo());
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode(), "The error code should be USER_NOT_FOUND.");
    }

}
