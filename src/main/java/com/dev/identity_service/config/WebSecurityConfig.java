package com.dev.identity_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private CustomJwtDecoder jwtDecoder;

    @Value("${jwt.secretKey}")
    private String SECRET; // This will be injected from application.yml

    private final String[] PUBLIC_ENDPOINTS = {"api/users", "auth/**"};

    @Bean
    public PasswordEncoder passwordEncoder() {
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
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // Map roles from the "roles" claim in the JWT

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF if using JWT or other stateless authentication
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(HttpMethod.GET, "/api/users/get-ip").permitAll() // Allow access to this endpoint
                        .requestMatchers(
                                HttpMethod.POST,
                                "/auth/token",
                                "/auth/introspect",
                                "/auth/logout",
                                "/auth/refresh",
                                "/api/users") // Allow POST access to these endpoints
                        .permitAll()
                        .anyRequest() // All other requests require authentication
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer ->
                        jwtConfigurer.decoder(jwtDecoder) // Use the JwtDecoder bean
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Set custom JWT authentication converter
                ).authenticationEntryPoint(new JwtAuthenticationEntryPoint())) // Custom entry point for unauthenticated users
                .addFilterBefore(corsFilter(), CorsFilter.class); // Explicitly add CORS filter

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:3000"); // Replace with your frontend URL
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsFilter(source);
    }
    //    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.setAllowedOrigins(List.of("http://localhost:3000", "https://another-origin.com"));
//        config.setAllowedHeaders(List.of("Content-Type", "Authorization"));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/api/**", config);
//
//        return new CorsFilter(source);
//    }

}
