package com.unilend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users") // The table name must match the table name in V1 SQL.
@Data // Lombok auto generate Getter/Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; //This is an ENCRYPTED password (BCrypt), not a plain text password.

    @Column(name = "full_name", nullable = false)
    private String fullName; // Hibernate automatically maps to full_name (camelCase -> snake_case)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
