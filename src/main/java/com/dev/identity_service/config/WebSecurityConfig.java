package com.dev.identity_service.config;


import com.dev.identity_service.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig
{

    private CustomJwtDecoder jwtDecoder;

    @Value("${jwt.secretKey}")
    private String SECRET; // This will be injected from application.yml


    private final String[] PUBLIC_ENDPOINTS = {
            "api/users",
            "auth/**"
    };


    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder(10);
    }

//    @Bean
//    public JwtDecoder jwtDecoder()
//    {
//        // Use the SECRET key to create an HMAC key
//        byte[] secretKeyBytes = SECRET.getBytes();
//        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "HmacSHA256");//HS256 (HMAC with SHA-256).
//
//        // Configure NimbusJwtDecoder with the symmetric key
//        return NimbusJwtDecoder
//                .withSecretKey(secretKeySpec)
//                .build();
//    }


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter()
    {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // Map roles from the "roles" claim in the JWT

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF if using JWT or other stateless authentication
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers(PUBLIC_ENDPOINTS).permitAll()  // Allow access to public endpoints
                        //.requestMatchers(HttpMethod.GET, "/api/users").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/users/get-ip").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/token","/auth/introspect","/auth/logout").permitAll()
                        .anyRequest().authenticated()               // Require authentication for other endpoints
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtConfigurer ->
                                jwtConfigurer.decoder(jwtDecoder) // Use the JwtDecoder bean
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );


        return http.build();
    }


}
