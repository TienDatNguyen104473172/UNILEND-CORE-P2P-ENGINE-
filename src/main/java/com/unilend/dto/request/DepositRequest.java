//Customers who want to deposit money must let us know how much they want to deposit.
package com.unilend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "10.00", message = "Minimum deposit amount is 10.00") // The minimum deposit amount is 10 AUD.
    private BigDecimal amount;

    // Later we can add: bankSource, paymentMethod...
}