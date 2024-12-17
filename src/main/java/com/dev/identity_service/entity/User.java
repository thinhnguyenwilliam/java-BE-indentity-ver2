package com.dev.identity_service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;


    @Column(name = "username",
            unique = true, nullable = false,
            columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;



    String password;
    String firstName;
    String lastName;

    LocalDate dob;

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime updatedAt;

    @ManyToMany
    Set<Role> roles;
}
