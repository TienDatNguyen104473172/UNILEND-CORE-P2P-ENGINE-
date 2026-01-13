//Upon successful registration, you will receive a free empty wallet.
package com.unilend.service;

import com.unilend.entity.User;
import com.unilend.entity.Wallet;
import com.unilend.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    // This function will be called when the user successfully registers.
    @Transactional // Ensuring data security
    public Wallet createWalletForUser(User user) {
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO) // Initial balance = 0
                .currency("AUD")          // Australian dollars by default
                .build();

        return walletRepository.save(wallet);
    }
}