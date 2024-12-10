package com.dev.identity_service.controller;


import com.dev.identity_service.dto.request.UserCreationRequest;
import com.dev.identity_service.dto.request.UserUpdateRequest;
import com.dev.identity_service.dto.response.ApiResponse;
import com.dev.identity_service.dto.response.UserResponse;
import com.dev.identity_service.entity.User;
import com.dev.identity_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class UserController
{
    UserService userService;


    @GetMapping("/get-ip")
    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // Convert IPv6 localhost to IPv4 localhost
        if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }

        //curl -X GET http://localhost:8081/identity/api/users/get-ip
        return "Client IP Address: " + ipAddress;
    }

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request)
    {
        log.info("Test Hello Controller: Creating user");

        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Creating user successfully yeah man");
        apiResponse.setCode(1000);
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers()
    {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority ->
                log.info("GrantedAuthority: {}", grantedAuthority));

        // Call the service to fetch users and map them to responses
        List<UserResponse> users = userService.getUsers();
        log.info("UserResponse List: {}", users); // Debug log

        // Return the response
        return ApiResponse.<List<UserResponse>>builder()
                .code(1000) // Set the HTTP status code (200 for success)
                .message("Users retrieved successfully")
                .result(users)
                .build();
    }



    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }


    @PatchMapping("/{id}")
    public UserResponse updateUser(@PathVariable String id, @RequestBody UserUpdateRequest request){
        return userService.updateUser(id, request);
    }


    @DeleteMapping("/{ids}")
    public List<User> deleteUsers(@PathVariable List<String> ids)
    {
        return userService.deleteUsers(ids);
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                //.code(1000)
                //.message("Request successful")
                .result(userService.getMyInfo())
                .build();

    }

}
