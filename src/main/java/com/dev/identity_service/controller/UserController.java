package com.dev.identity_service.controller;


import com.dev.identity_service.dto.request.UserCreationRequest;
import com.dev.identity_service.dto.request.UserUpdateRequest;
import com.dev.identity_service.entity.User;
import com.dev.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserController
{
    UserService userService;


    @PostMapping
    public User createUser(@RequestBody @Valid UserCreationRequest request)
    {
        return userService.createUser(request);
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getUsers();
    }


    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }


    @PatchMapping("/{id}")
    public User updateUser(@PathVariable String id, @RequestBody UserUpdateRequest request){
        return userService.updateUser(id, request);
    }


    @DeleteMapping("/{ids}")
    public List<User> deleteUsers(@PathVariable List<String> ids)
    {
        return userService.deleteUsers(ids);
    }


}
