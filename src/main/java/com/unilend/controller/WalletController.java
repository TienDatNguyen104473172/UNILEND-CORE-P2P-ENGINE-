//This is where the Postman/Frontend receives requests, verifies "Who's calling?", and forwards the command to the Service for processing.
package com.unilend.controller;

import com.unilend.dto.request.DepositRequest;
import com.unilend.dto.request.TransferRequest;
import com.unilend.dto.request.WithdrawRequest;
import com.unilend.entity.Wallet;
import com.unilend.security.CustomUserDetails;
import com.unilend.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // API: Top-up
    // POST /api/wallet/deposit
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody DepositRequest request) {

        // 1.get the login information of the currently logged-in user from the Token (Security).
        // Never trust IDs sent from the client; always get them from the SecurityContext.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        // 2. Call Service to handle it.
        Wallet updatedWallet = walletService.depositFund(userId, request);

        // 3. Return result
        return ResponseEntity.ok(updatedWallet);
    }

    // New API: withdrawal
    // POST /api/wallet/withdraw
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid @RequestBody WithdrawRequest request) {
        // 1. get User ID from Token (security)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        // 2. call service to withdraw
        Wallet updatedWallet = walletService.withdrawFund(userId, request);

        // 3. return result
        return ResponseEntity.ok(updatedWallet);
    }

    // API: sending money
    // POST /api/wallet/transfer
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest request) {
        // 1. get sender ID from token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        assert userDetails != null;
        Long senderId = userDetails.getUser().getId();

        // 2. call Service
        walletService.transferFunds(senderId, request);
        return ResponseEntity.ok("Transfer successful! (Chuyển tiền thành công)");
    }
}
