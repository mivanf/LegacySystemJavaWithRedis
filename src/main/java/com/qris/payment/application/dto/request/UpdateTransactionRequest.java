package com.qris.payment.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update transaction request")
public class UpdateTransactionRequest {

    @DecimalMin(value = "0", inclusive = false, message = "Amount must be greater than 0")
    @Schema(description = "Updated amount", example = "75000")
    private BigDecimal amount;

    @Schema(description = "Updated status", example = "SUCCESS", allowableValues = {"PENDING", "SUCCESS", "FAILED"})
    private String status;
}
