package com.dev.identity_service.config;

import com.dev.identity_service.entity.User;
import com.dev.identity_service.repository.RoleRepository;
import com.dev.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class ApplicationInitConfig
{
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;



    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            // Create admin role if not present
//            Role adminRole = roleRepository.findById("ADMIN").orElseGet(() -> {
//                Role role = Role.builder()
//                        .name("ADMIN")
//                        .description("Administrator role with full privileges")
//                        .build();
//                return roleRepository.save(role);
//            });

            // Check if the admin user already exists
            if (userRepository.findByUsername("admin").isEmpty()) {
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123")) // Use a secure default password
                ///        .roles(Set.of(adminRole))
                        .build();

                // Save the user
                userRepository.save(user);
                log.warn("Admin user created with username: admin");
            }
        };
    }

}
