package com.dev.identity_service.service;


import com.dev.identity_service.entity.InvalidatedToken;
import com.dev.identity_service.repository.InvalidatedTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Transactional
@Slf4j
public class InvalidatedTokenService
{
    InvalidatedTokenRepository invalidatedTokenRepository;

//    public InvalidatedToken saveToken(InvalidatedToken token)
//    {
//        log.info("Saving invalidated token: {}", token.getId());
//        return invalidatedTokenRepository.save(token);
//    }
//
//    public boolean isTokenExpired(String tokenId)
//    {
//        Optional<InvalidatedToken> token = invalidatedTokenRepository.findById(tokenId);
//        if (token.isPresent())
//        {
//            boolean expired = token.get().getExpiryTime().before(new java.util.Date());
//            log.info("Token with ID {} is expired: {}", tokenId, expired);
//            return expired;
//        }
//        log.warn("Token with ID {} not found", tokenId);
//        return true; // Treat missing tokens as expired for security.
//    }
//
//    public Optional<InvalidatedToken> findById(String tokenId)
//    {
//        log.info("Fetching invalidated token by ID: {}", tokenId);
//        return invalidatedTokenRepository.findById(tokenId);
//    }
}
