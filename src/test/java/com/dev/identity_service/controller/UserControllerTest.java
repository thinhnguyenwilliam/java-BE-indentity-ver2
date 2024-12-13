package com.dev.identity_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.dev.identity_service.dto.request.UserCreationRequest;
import com.dev.identity_service.dto.response.UserResponse;
import com.dev.identity_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonRequest = objectMapper.writeValueAsString(userCreationRequest);

        when(userService.createUser(any())).thenReturn(userResponse);

        // When and Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Creating user successfully yeah man"))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value("12cf2543b476765"))
                .andExpect(jsonPath("$.result.username").value("JohnHanno"))
                .andExpect(jsonPath("$.result.firstName").value("John"))
                .andExpect(jsonPath("$.result.lastName").value("Hanno"))
                .andExpect(jsonPath("$.result.dob").value("1990-01-01"));

        // Verify that the service method was called once
        verify(userService, times(1)).createUser(any());
    }

    @Test
    void createUser_missingUsername_badRequest() throws Exception {
        // Given: User request without username
        UserCreationRequest invalidRequest = UserCreationRequest.builder()
                .username("J")
                .firstName("John")
                .lastName("Hanno")
                .password("12334655")
                .dob(LocalDate.of(1990, 1, 1))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);

        // When and Then: Expect bad request (400)
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(9998))
                .andExpect(jsonPath("$.message").value("Username must be at least 3 characters"));
    }

    @Test
    void createUser_invalidPassword_badRequest() throws Exception {
        // Given: User request with invalid password format (e.g., too short)
        UserCreationRequest invalidRequest = UserCreationRequest.builder()
                .username("John Marry ANH")
                .firstName("John")
                .lastName("Hanno")
                .password("12")
                .dob(LocalDate.of(1990, 1, 1))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);

        // When and Then: Expect bad request (400) due to validation failure
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}
