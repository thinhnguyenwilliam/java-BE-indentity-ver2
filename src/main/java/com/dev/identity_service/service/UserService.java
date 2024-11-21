package com.dev.identity_service.service;


import com.dev.identity_service.dto.request.UserCreationRequest;
import com.dev.identity_service.dto.request.UserUpdateRequest;
import com.dev.identity_service.entity.User;
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

    public User createUser(UserCreationRequest request){
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());


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


    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    public User updateUser(String id, UserUpdateRequest request)
    {
        // Fetch the user by ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        // Save and return the updated user
        return userRepository.save(user);
    }
}
