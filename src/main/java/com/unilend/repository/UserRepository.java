package com.unilend.repository;

import com.unilend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find users by email (because people enter their email address when logging in).
    Optional<User> findByEmail(String email);

    // Check if the email address already exists (Used during registration)
    Boolean existsByEmail(String email);
}