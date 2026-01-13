//A wallet can hold thousands of transactions (One-to-Many).
package com.unilend.entity;

import com.unilend.common.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Multiple transactions belong to one wallet.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private BigDecimal amount; // Amount fluctuates (+ or -)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // DEPOSIT, WITHDRAWAL...

    // References are used for tracking (e.g., This transaction belongs to loan ID number 5).
    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "reference_type")
    private String referenceType; // LOAN, INVESTMENT...

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}