package com.dev.identity_service.util;

import com.dev.identity_service.entity.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil
{

    @Value("${jwt.secretKey}")
    private String SECRET; // This will be injected from application.yml

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME; // This will be injected from application.yml



    private String buildScope(User user) {
        // Extract roles and permissions, and join them into a single string
        String roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName().toUpperCase()) // Prefix with ROLE_
                .distinct()
                .collect(Collectors.joining(" "));

        String permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName().toUpperCase())
                .distinct()
                .collect(Collectors.joining(" "));

        // Combine roles and permissions into a single scope string
        return String.join(" ", roles, permissions);
    }





    // Instance method instead of static
    public String generateToken(User user) {
        //properties => claims
        //Map<String, Object> claimsOption = new HashMap<>();
        //claimsOption.put("phoneNumber", "1234");



        try {
            // Create the JWT claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("Dev_William") // Optional
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .jwtID(String.valueOf(UUID.randomUUID()))
                    .claim("roles", buildScope(user))
                    .build();


//            // Start building the JWT claims set
//            JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder()
//                    .subject(username)
//                    .issuer("Dev_William") // Optional
//                    .issueTime(new Date())
//                    .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_TIME));
//
//            // Add each claim from the map to the JWT claims
//            for (Map.Entry<String, Object> entry : claimsOption.entrySet()) {
//                claimsSetBuilder.claim(entry.getKey(), entry.getValue());
//            }
//
//            // Build the claims set
//            JWTClaimsSet claimsSet = claimsSetBuilder.build();


            // Create the JWS header and specify the HMAC algorithm
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            // Create the signed JWT
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            // Sign the JWT using the HMAC secret key
            JWSSigner signer = new MACSigner(SECRET.getBytes());
            signedJWT.sign(signer);

            // Serialize the token to a compact form
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    // Instance method instead of static
    private boolean isTokenExpired(SignedJWT signedJWT) throws ParseException {
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        return expiration != null && expiration.before(new Date());
    }

    // Instance method instead of static
    public boolean validateToken(String token) {
        try {
            // Parse the token
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Create the verifier using the secret key
            JWSVerifier verifier = new MACVerifier(SECRET.getBytes());

            // Verify the token's signature and check expiration
            return signedJWT.verify(verifier) && !isTokenExpired(signedJWT);
        } catch (JOSEException | ParseException e) {
            // Log the exception (optional) and return false
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }


    public String extractScope(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getStringClaim("roles");
    }
}
