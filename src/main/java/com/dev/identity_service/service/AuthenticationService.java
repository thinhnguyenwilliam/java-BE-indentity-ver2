package com.dev.identity_service.service;


import com.dev.identity_service.dto.request.AuthenticationRequest;
import com.dev.identity_service.enums.ErrorCode;
import com.dev.identity_service.exception.AppException;
import com.dev.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Transactional
public class AuthenticationService
{
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;


    public Boolean authenticate(AuthenticationRequest request)
    {
        var user=userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return passwordEncoder.matches(request.getPassword(), user.getPassword());
    }
}
