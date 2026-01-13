package com.unilend.repository;

import com.unilend.entity.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, Long> {
    // Retrieve the transaction history of a wallet, sort by the most recent transactions at the top.
    List<LedgerTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);
}