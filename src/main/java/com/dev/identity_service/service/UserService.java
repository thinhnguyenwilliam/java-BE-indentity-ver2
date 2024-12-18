package com.dev.identity_service.service;

import java.util.*;
import java.util.stream.Collectors;

import com.dev.identity_service.constant.PredefinedRole;
import com.dev.identity_service.entity.Role;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.identity_service.dto.request.UserCreationRequest;
import com.dev.identity_service.dto.request.UserUpdateRequest;
import com.dev.identity_service.dto.response.UserResponse;
import com.dev.identity_service.entity.User;
import com.dev.identity_service.enums.ErrorCode;
import com.dev.identity_service.exception.AppException;
import com.dev.identity_service.mapper.UserMapper;
import com.dev.identity_service.repository.RoleRepository;
import com.dev.identity_service.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public UserResponse createUser(UserCreationRequest request)
    {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);


        // Save the user to the database
        User savedUser;
        try {
            savedUser = userRepository.save(user);
            // Map the saved user to a response DTO and return
            return userMapper.toUserResponse(savedUser);
        } catch (DataIntegrityViolationException e) {
            // Log the exception and throw a custom application exception
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    //@PreAuthorize("hasAuthority('CREATE_DATA')")
    public List<UserResponse> getUsers() {
        log.info("inside getUsers method");

        return userRepository.findAll().stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

    public List<User> deleteUsers(List<String> ids) {
        // Fetch all users matching the given IDs
        List<User> usersToDelete = userRepository.findAllById(ids);

        // Validate if all provided IDs are found
        if (usersToDelete.size() != ids.size()) {
            throw new RuntimeException("Some users were not found for the given IDs: " + ids);
        }

        // Perform deletion
        userRepository.deleteAll(usersToDelete);

        // Return deleted users
        return usersToDelete;
    }

    @PostAuthorize("returnObject.username == authentication.name or hasRole('ADMIN')")
    public UserResponse getUserById(String id) {
        log.info("inside getUsers method with id: {}", id);

        // Fetch the User entity from the repository
        User user =
                userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Map the User entity to UserResponse and calculate fullName
        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));

        return userResponse;
    }

    public UserResponse updateUser(String id, UserUpdateRequest request) {
        // Fetch the user by ID
        User user =
                userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Map update request data to the user entity
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        // Save the updated user entity
        user = userRepository.save(user);

        // Map the updated entity to the response DTO
        UserResponse response = userMapper.toUserResponse(user);

        // Optional: Compute fullName dynamically if needed
        response.setFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));

        return response;
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        String username = authentication.getName(); // Retrieve the username
        User user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user); // Map user entity to response DTO
    }
}
