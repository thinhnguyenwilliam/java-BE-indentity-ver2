package com.dev.identity_service.service;


import com.dev.identity_service.dto.request.UserCreationRequest;
import com.dev.identity_service.entity.User;
import com.dev.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Transactional
public class UserService
{
    UserRepository userRepository;

    public User createUser(UserCreationRequest request)
    {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());


        return userRepository.save(user);
    }
}
