//Upon successful registration, you will receive a free empty wallet.
package com.unilend.service;

import com.unilend.common.enums.TransactionType;
import com.unilend.dto.request.TransferRequest;
import com.unilend.dto.request.DepositRequest;
import com.unilend.dto.request.WithdrawRequest;
import com.unilend.dto.response.TransactionResponse;
import com.unilend.entity.LedgerTransaction;
import com.unilend.entity.User;
import com.unilend.entity.Wallet;
import com.unilend.repository.LedgerTransactionRepository;
import com.unilend.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.util.stream.Collectors;

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

    @Transactional //rollback service functionality
    public Wallet withdrawFund(Long userId, WithdrawRequest request) {
        // B1: search wallets
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // B2: check balance (important)
        // wallet.getBalance().compareTo(request.getAmount()) < 0 means Balance < Amount
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance (Không đủ tiền để rút)");
        }

        // B3: Record in the General Ledger (Type: WITHDRAWAL)
        LedgerTransaction transaction = LedgerTransaction.builder()
                .wallet(wallet)
                .amount(request.getAmount().negate()) // Record a negative number to represent the amount of money withdrawn (Optional, or record a positive number as per convention).
                .type(TransactionType.WITHDRAWAL)     // Type: Withdrawal
                .referenceId("SELF-WITHDRAW-" + System.currentTimeMillis())
                .referenceType("BANK_TRANSFER")
                .build();

        ledgerTransactionRepository.save(transaction);

        // B4: Deduct money
        BigDecimal newBalance = wallet.getBalance().subtract(request.getAmount());
        wallet.setBalance(newBalance);

        // B5: Save new balance to wallet
        return walletRepository.save(wallet);
    }

    @Transactional(rollbackFor = Exception.class) // Roll back if any errors occur.
    public void transferFunds(Long fromUserId, TransferRequest request) {

        // 1. Validate: Do not transfer to yourself.
        if (fromUserId.equals(request.getToUserId())) {
            throw new RuntimeException("Cannot transfer money to yourself (Không thể tự chuyển tiền cho mình)");
        }

        // 2. Get the sender's wallet address. (Sender)
        Wallet senderWallet = walletRepository.findByUserId(fromUserId)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        // 3. Get the receiver's wallet address. (Receiver)
        Wallet receiverWallet = walletRepository.findByUserId(request.getToUserId())
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        // 4. check sender's wallet balance
        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // 5. PROCESS TRANSACTIONS
        BigDecimal amount = request.getAmount();

        // 5.1: DEDUCT THE DEPOSIT FROM THE SENDER (Debit)
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        walletRepository.save(senderWallet);

        // Record in the ledger for the sender (Cash outflow - Negative amount)
        LedgerTransaction senderLog = LedgerTransaction.builder()
                .wallet(senderWallet)
                .amount(amount.negate())
                .type(TransactionType.TRANSFER)
                .referenceId("TRF-OUT-" + System.currentTimeMillis())
                .referenceType("USER_TRANSFER_TO_" + request.getToUserId())
                .build();
        ledgerTransactionRepository.save(senderLog);

        // 5.2: ADD THE receiver's AMOUNT (Credit)
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
        walletRepository.save(receiverWallet);

        // Record in the ledger for the receiver (Cash Inflow - Positive Amount)
        LedgerTransaction receiverLog = LedgerTransaction.builder()
                .wallet(receiverWallet)
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .referenceId("TRF-IN-" + System.currentTimeMillis())
                .referenceType("USER_TRANSFER_FROM_" + fromUserId)
                .build();
        ledgerTransactionRepository.save(receiverLog);
    }

    // Import TransactionResponse, List, Collectors, v.v.
    public List<TransactionResponse> getTransactionHistory(Long userId) {
        // 1. find user wallet
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // 2. Get the list of transactions from the database.
        List<LedgerTransaction> transactions = ledgerTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());

        // 3. Convert from Entity to DTO (Dùng Stream cho ngầu)
        return transactions.stream()
                .map(tx -> TransactionResponse.builder()
                        .referenceId(tx.getReferenceId())
                        .amount(tx.getAmount())
                        .type(tx.getType())
                        .createdAt(tx.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}