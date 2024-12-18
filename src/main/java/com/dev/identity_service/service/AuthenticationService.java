package com.dev.identity_service.service;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class AuthenticationService {
    @NonFinal
    @Value("${jwt.secretKey}")
    protected String SECRET;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    JwtTokenUtil jwtTokenUtil;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    InvalidatedTokenRepository invalidatedTokenRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        logger.info("SECRET in yam-prod is: {}", SECRET);
        // Find the user by username
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Check if the provided password matches the stored password
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Generate a JWT token
        String token = jwtTokenUtil.generateToken(user);

        // Return the token and authentication status wrapped in the AuthenticationResponse DTO
        return AuthenticationResponse.builder().authenticated(true).token(token).build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        String token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException | JOSEException | ParseException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        // Parse and verify the token
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SECRET.getBytes());

        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Extract claims
        var claims = signedJWT.getJWTClaimsSet();
        Date expirationTime = claims.getExpirationTime();
        if (isRefresh) {
            Date issueTime = claims.getIssueTime();
            expirationTime = new Date(issueTime
                    .toInstant()
                    .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                    .toEpochMilli());
        }

        if (expirationTime == null || expirationTime.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        // Check if token is invalidated
        if (invalidatedTokenRepository.existsById(claims.getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        try {
            var signedToken = verifyToken(request.getToken(), true);
            // Extract the JWT ID (jti) for identifying the token
            String jti = signedToken.getJWTClaimsSet().getJWTID();
            Date expirationTime = signedToken.getJWTClaimsSet().getExpirationTime();

            // Save invalidated token to the repository
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jti)
                    .expiryTime(expirationTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            logger.info("Token is expired cry baby");
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);
        var jti = signedJWT.getJWTClaimsSet().getJWTID();
        var expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Save invalidated token to the repository
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jti).expiryTime(expirationTime).build();
        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Generate a JWT token
        String token = jwtTokenUtil.generateToken(user);

        // Return the token and authentication status wrapped in the AuthenticationResponse DTO
        return AuthenticationResponse.builder().authenticated(true).token(token).build();
    }
}
