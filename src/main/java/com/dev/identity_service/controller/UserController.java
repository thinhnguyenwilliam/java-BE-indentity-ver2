package com.dev.identity_service.controller;


import com.dev.identity_service.dto.request.UserCreationRequest;
import com.dev.identity_service.entity.User;
import com.dev.identity_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserController
{
    UserService userService;


    @PostMapping("/users")
    public User addUser(@RequestBody UserCreationRequest request)
    {
        return userService.createUser(request);
    }
}
