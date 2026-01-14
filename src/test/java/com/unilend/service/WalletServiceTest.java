//If the deposit test adds real money to the database, all the data will be corrupted.
//Use Mockito to create "mock databases".
package com.unilend.service;

import com.unilend.dto.request.DepositRequest;
import com.unilend.entity.LedgerTransaction;
import com.unilend.entity.User;
import com.unilend.entity.Wallet;
import com.unilend.repository.LedgerTransactionRepository;
import com.unilend.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import com.unilend.dto.request.WithdrawRequest;
import com.unilend.dto.request.TransferRequest;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class) // Activate Mockito
public class WalletServiceTest {

    @Mock // Simulated Repository (Not calling a real database)
    private WalletRepository walletRepository;

    @Mock // Simulated Repository
    private LedgerTransactionRepository ledgerTransactionRepository;

    @InjectMocks // Inject the above mocks into the service that needs testing.
    private WalletService walletService;

    // Sample data for general use in tests.
    private User user;
    private Wallet wallet;

    @BeforeEach // Run this before each test to reset the data.
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .fullName("Test User")
                .build();

        wallet = Wallet.builder()
                .id(100L)
                .user(user)
                .balance(new BigDecimal("1000.00")) // The wallet has 1000 AUD in it.
                .currency("AUD")
                .build();
    }

    @Test
    void testDeposit_Success() {
        // 1. ARRANGE (Chuẩn bị)
        DepositRequest request = new DepositRequest();
        request.setAmount(new BigDecimal("500.00"));

        // "Dạy" Mock Repository:
        // "If anyone asks to find User ID 1, give them the fake wallet I created in the setUp function."
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

        // "If someone calls the 'save wallet' function, just return the wallet itself (to avoid a Null error)."
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. ACT (Hành động)
        // Call the actual function
        Wallet updatedWallet = walletService.depositFund(1L, request);

        // 3. ASSERT (Kiểm tra)
        // Old balance 1000 + Top-up 500 = Expect 1500
        assertEquals(new BigDecimal("1500.00"), updatedWallet.getBalance());

        // Check if the `save` function to the database was actually called.
        verify(walletRepository).save(any(Wallet.class)); // call to save your wallet.
        verify(ledgerTransactionRepository).save(any(LedgerTransaction.class)); // call to save your ledger.
    }

    @Test
    void testWithdraw_InsufficientBalance_ThrowException() {
        // 1. ARRANGE
        WithdrawRequest request = new WithdrawRequest();
        request.setAmount(new BigDecimal("5000.00")); // Tham lam: Withdraw 5000

        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

        // 2. ACT & ASSERT (Hành động và Kiểm tra cùng lúc)
        // assertThrows: I expect this code to throw a "Throw Exception".
        RuntimeException exception = assertThrows(RuntimeException.class, () -> walletService.withdrawFund(1L, request));

        // Check if the error message matches what you wrote in the Service.
        assertEquals("Insufficient balance (Không đủ tiền để rút)", exception.getMessage());
    }

    @Test
    void testTransfer_Success() {
        //1. ARRANGE (Chuẩn bị bối cảnh)

        // Create Sender's Wallet (A) - Has 1000 AUD
        Wallet senderWallet = Wallet.builder()
                .id(1L)
                .balance(new BigDecimal("1000.00"))
                .build();

        // Create Recipient Wallet (B) - Has 0 AUD
        Wallet receiverWallet = Wallet.builder()
                .id(2L)
                .balance(new BigDecimal("0.00"))
                .build();

        // Create a request to transfer 500 VND.
        TransferRequest request = new TransferRequest();
        request.setToUserId(2L); // Chuyển cho B
        request.setAmount(new BigDecimal("500.00"));

        // Dạy Mockito:
        // Ask sender's wallet (ID 1) -> Return wallet A
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(senderWallet));
        // Ask recipient's wallet address (ID 2) -> Return to wallet B
        when(walletRepository.findByUserId(2L)).thenReturn(Optional.of(receiverWallet));

        // 2. ACT (Hành động)
        // Call the money transfer function: A transfers money to B (the request already contains B's ID).
        walletService.transferFunds(1L, request);

        // 3. ASSERT (Kiểm tra kết quả)

        // Check the balance in your virtual wallet (Java Object).
        // wallet A: 1000 - 500 = 500
        assertEquals(new BigDecimal("500.00"), senderWallet.getBalance());
        // wallet B: 0 + 500 = 500
        assertEquals(new BigDecimal("500.00"), receiverWallet.getBalance());

        // Check if the database has been called SAVE twice.
        verify(walletRepository).save(senderWallet);   // Phải lưu ví A
        verify(walletRepository).save(receiverWallet); // Phải lưu ví B

        // Check if there are two entries in the ledger (one for subtraction and one for addition).
        //verify(ledgerTransactionRepository, times(2)).save(any(LedgerTransaction.class));
    }
}