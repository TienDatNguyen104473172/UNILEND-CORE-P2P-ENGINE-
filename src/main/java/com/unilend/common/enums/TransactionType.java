//The system clearly defines the types of transactions available (Deposit, Withdrawal, Transfer, etc.).
package com.unilend.common.enums;

public enum TransactionType {
    DEPOSIT,    // deposit money
    WITHDRAWAL, // withdraw money
    LOAN_DISBURSEMENT, // Loan disbursement (Receiving loan funds)
    LOAN_REPAYMENT,    // Pay off debt (Deduct money)
    INVESTMENT,        // Investment (Deduct money)
    INVESTMENT_RETURN, // Receive profits
    FEE                // Service fees
}
