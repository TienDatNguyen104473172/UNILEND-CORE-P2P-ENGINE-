//When a user asks about their history, we shouldn't return the entire Entity (because it contains sensitive or redundant information). We need to create a DTO to return only what the user needs to see.
package com.unilend.dto.response;

import com.unilend.common.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private String referenceId; // Transaction ID
    private BigDecimal amount;  // Amount (+ or -)
    private TransactionType type; // Deposit/Withdraw/Transfer
    private LocalDateTime createdAt; // time
    // Có thể thêm: Mô tả, Số dư sau giao dịch...
}
