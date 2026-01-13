package com.unilend.repository;

import com.unilend.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    // Find the wallet of a specific user.
    Optional<Wallet> findByUserId(Long userId);
}