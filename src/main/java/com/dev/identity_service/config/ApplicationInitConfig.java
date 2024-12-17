package com.dev.identity_service.config;

import com.dev.identity_service.constant.PredefinedRole;
import com.dev.identity_service.entity.Role;
import com.dev.identity_service.entity.User;
import com.dev.identity_service.repository.RoleRepository;
import com.dev.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    @NonFinal
    @Value("${app.admin.username:admin}")
    String adminUsername;

    @NonFinal
    @Value("${app.admin.password:admin123}")
    String adminPassword;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    @Transactional
    public ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            // Initialize roles
            Role adminRole = roleRepository.findById(PredefinedRole.ADMIN_ROLE)
                    .orElseGet(() -> {
                        log.info("Creating ADMIN role...");
                        return roleRepository.save(
                                Role.builder()
                                        .name(PredefinedRole.ADMIN_ROLE)
                                        .description("Administrator role with full privileges")
                                        .build()
                        );
                    });

            Role userRole = roleRepository.findById(PredefinedRole.USER_ROLE)
                    .orElseGet(() -> {
                        log.info("Creating USER role...");
                        return roleRepository.save(
                                Role.builder()
                                        .name(PredefinedRole.USER_ROLE)
                                        .description("User role with standard privileges")
                                        .build()
                        );
                    });

            // Check and create the admin user
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                log.info("Creating admin user...");
                User adminUser = User.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .roles(Set.of(adminRole))
                        .build();

                userRepository.save(adminUser);
                log.warn("Admin user created with username: {}", adminUsername);
            } else {
                log.info("Admin user already exists. Skipping creation.");
            }
        };
    }
}
