//Upon successful registration, you will receive a free empty wallet.
package com.unilend.service;

import com.unilend.common.enums.TransactionType;
import com.unilend.dto.request.DepositRequest;
import com.unilend.entity.LedgerTransaction;
import com.unilend.entity.User;
import com.unilend.entity.Wallet;
import com.unilend.repository.LedgerTransactionRepository;
import com.unilend.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final LedgerTransactionRepository ledgerTransactionRepository;

    public WalletService(WalletRepository walletRepository, LedgerTransactionRepository ledgerTransactionRepository) {
        this.walletRepository = walletRepository;
        this.ledgerTransactionRepository = ledgerTransactionRepository;
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

    // 👇 2. DEPOSIT MONEY
    @Transactional // IMPORTANT: Ensure both wallet top-ups and ledger entries are successful, or both are rolled back.
    public Wallet depositFund(Long userId, DepositRequest request) {
        // B1: Find User's wallet
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user id: " + userId));

        // B2: Record in the Ledger first (Accounting principle: Record first, count later)
        LedgerTransaction transaction = LedgerTransaction.builder()
                .wallet(wallet)
                .amount(request.getAmount())          // Positive amount (+)
                .type(TransactionType.DEPOSIT)        // Type: Top-up
                .referenceId("SELF-DEPOSIT-" + System.currentTimeMillis()) // Temporary reference code
                .referenceType("BANK_TRANSFER")
                .build();

        ledgerTransactionRepository.save(transaction);

        // B3: Add money to your Wallet
        // balance = balance + amount
        BigDecimal newBalance = wallet.getBalance().add(request.getAmount());
        wallet.setBalance(newBalance);

        // B4: Save wallet
        return walletRepository.save(wallet);
    }
}