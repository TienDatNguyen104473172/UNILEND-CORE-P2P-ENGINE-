//We need to know who to transfer the money to (toUserId) and how much (amount).
package com.unilend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotNull(message = "Receiver ID cannot be null")
    private Long toUserId; // To whom should I transfer it?

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "1.00", message = "Minimum transfer amount is 1.00")
    private BigDecimal amount; // How much should I transfer?
}
