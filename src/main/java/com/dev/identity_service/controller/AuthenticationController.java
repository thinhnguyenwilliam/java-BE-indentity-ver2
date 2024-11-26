package com.dev.identity_service.controller;


import com.dev.identity_service.dto.request.AuthenticationRequest;
import com.dev.identity_service.dto.response.ApiResponse;
import com.dev.identity_service.dto.response.AuthenticationResponse;
import com.dev.identity_service.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController
{
    AuthenticationService authenticationService;


    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> logIn(@RequestBody AuthenticationRequest request)
    {
        Boolean result=authenticationService.authenticate(request);

        // Determine the message based on the authentication result
        String message1 = result ? "Password correct, welcome!" : "Invalid credentials, please try again.";


        return ApiResponse.<AuthenticationResponse>builder()
                .code(1000) // Explicitly setting the code
                .message(message1)
                .result(AuthenticationResponse.builder()
                        .authenticated(result)
                        .build())
                .build();
    }

}
