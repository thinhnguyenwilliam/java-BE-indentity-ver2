package com.dev.identity_service.controller;


import com.dev.identity_service.dto.request.AuthenticationRequest;
import com.dev.identity_service.dto.request.IntrospectRequest;
import com.dev.identity_service.dto.response.ApiResponse;
import com.dev.identity_service.dto.response.AuthenticationResponse;
import com.dev.identity_service.dto.response.IntrospectResponse;
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


    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> logIn(@RequestBody AuthenticationRequest request)
    {
        // Call the authenticate method and get the AuthenticationResponse
        AuthenticationResponse authResponse = authenticationService.authenticate(request);

        // Determine the message based on the authentication result
        String message = authResponse.getAuthenticated()
                ? "Password correct, welcome!"
                : "Invalid credentials, please try again.";

        // Build and return the ApiResponse
        return ApiResponse.<AuthenticationResponse>builder()
                .code(1000) // Explicitly setting the code
                .message(message)
                .result(authResponse)
                .build();
    }


    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
    {
        // Call the introspect method and validate the token
        IntrospectResponse response = authenticationService.introspect(request);

        int codeDetermine = response.isValid() ? 200 : 400; // 200 for valid, 400 for invalid
        // Determine the message based on the validation result
        String message = response.isValid()
                ? "Token is valid."
                : "Token is invalid or expired.";

        // Build and return the ApiResponse
        return ApiResponse.<IntrospectResponse>builder()
                .code(codeDetermine)
                .message(message)
                .result(response)
                .build();
    }
}
