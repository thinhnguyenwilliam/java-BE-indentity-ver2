package com.dev.identity_service.config;

import com.dev.identity_service.dto.request.IntrospectRequest;
import com.dev.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;


@Component
public class CustomJwtDecoder implements JwtDecoder
{
    @Value("${jwt.secretKey}")
    private String SECRET;


    private final AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    public CustomJwtDecoder(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    @Override
    public Jwt decode(String token) throws JwtException
    {

        var response = authenticationService.introspect(
                IntrospectRequest.builder().token(token).build());

        //System.out.println("Introspection response where are you: " + response);
        if (!response.isValid()) throw new JwtException("Token invalid baby 1");

        if (Objects.isNull(nimbusJwtDecoder))
        {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET.getBytes(), "HS256");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}
