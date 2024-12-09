package com.dev.identity_service.service;

import com.dev.identity_service.dto.request.AuthenticationRequest;
import com.dev.identity_service.dto.request.IntrospectRequest;
import com.dev.identity_service.dto.request.LogoutRequest;
import com.dev.identity_service.dto.request.RefreshRequest;
import com.dev.identity_service.dto.response.AuthenticationResponse;
import com.dev.identity_service.dto.response.IntrospectResponse;
import com.dev.identity_service.entity.InvalidatedToken;
import com.dev.identity_service.enums.ErrorCode;
import com.dev.identity_service.exception.AppException;
import com.dev.identity_service.repository.InvalidatedTokenRepository;
import com.dev.identity_service.repository.UserRepository;
import com.dev.identity_service.util.JwtTokenUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class AuthenticationService
{
    @NonFinal
    @Value("${jwt.secretKey}")
    String SECRET;


    @NonFinal
    @Value("${jwt.expiration}")
    long EXPIRATION_TIME;


    Logger logger = LoggerFactory.getLogger(AuthenticationService.class);


    JwtTokenUtil jwtTokenUtil;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    InvalidatedTokenRepository invalidatedTokenRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Find the user by username
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Check if the provided password matches the stored password
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Generate a JWT token
        String token = jwtTokenUtil.generateToken(user);

        // Return the token and authentication status wrapped in the AuthenticationResponse DTO
        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request)
    {
        String token = request.getToken();
        boolean isValid = true;
        try{
            verifyToken(token);
        }catch (AppException | JOSEException | ParseException e){
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException
    {
        // Parse the token
        SignedJWT signedJWT = SignedJWT.parse(token);

        // Create the verifier using the secret key
        JWSVerifier verifier = new MACVerifier(SECRET.getBytes());
        // Verify the token's signature
        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Check expiration
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime == null || expirationTime.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;
    }

    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        var signedToken = verifyToken(request.getToken());

        // Extract the JWT ID (jti) for identifying the token
        String jti = signedToken.getJWTClaimsSet().getJWTID();
        Date expirationTime = signedToken.getJWTClaimsSet().getExpirationTime();


        // Save invalidated token to the repository
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expirationTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
    }

    public AuthenticationResponse refreshToken(RefreshRequest request)
            throws ParseException, JOSEException
    {
        var signedJWT=verifyToken(request.getToken());
        var jti = signedJWT.getJWTClaimsSet().getJWTID();
        var expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Save invalidated token to the repository
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expirationTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

        var username=signedJWT.getJWTClaimsSet().getSubject();
        var user=userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Generate a JWT token
        String token = jwtTokenUtil.generateToken(user);

        // Return the token and authentication status wrapped in the AuthenticationResponse DTO
        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();
    }
}
