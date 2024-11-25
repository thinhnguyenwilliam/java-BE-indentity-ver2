package com.dev.identity_service.service;


import com.dev.identity_service.dto.request.UserCreationRequest;
import com.dev.identity_service.dto.request.UserUpdateRequest;
import com.dev.identity_service.dto.response.UserResponse;
import com.dev.identity_service.entity.User;
import com.dev.identity_service.enums.ErrorCode;
import com.dev.identity_service.exception.AppException;
import com.dev.identity_service.mapper.UserMapper;
import com.dev.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Transactional
public class UserService
{
    UserRepository userRepository;
    UserMapper userMapper;

    public User createUser(UserCreationRequest request)
    {
        if(userRepository.existsByUsername(request.getUsername())){
             throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
            //throw new RuntimeException("ErrorCode.USER_ALREADY_EXISTS sky");
        }

        User user = userMapper.toUser(request);
        return userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public List<User> deleteUsers(List<String> ids)
    {
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


    public UserResponse getUserById(String id)
    {
        // Fetch the User entity from the repository
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Map the User entity to UserResponse and calculate fullName
        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));

        return userResponse;
    }

    public UserResponse updateUser(String id, UserUpdateRequest request)
    {
        // Fetch the user by ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Map update request data to the user entity
        userMapper.updateUser(user, request);

        // Save the updated user entity
        user = userRepository.save(user);

        // Map the updated entity to the response DTO
        UserResponse response = userMapper.toUserResponse(user);

        // Optional: Compute fullName dynamically if needed
        response.setFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));

        return response;
    }
}
