//Receive the amount you wish to withdraw.
package com.unilend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {
    @NotNull(message = "Amount cannot be null") // cannot leave a blank
    @DecimalMin(value = "1.00", message = "Minimum withdrawal amount is 1.00") // the withdrawal amount is at least 1.00 aud
    private BigDecimal amount;
}