package com.qris.payment.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Transaction status response")
public class TransactionStatusResponse {

    @Schema(description = "Transaction identifier")
    private String transaction_id;

    @Schema(description = "Current status", example = "SUCCESS")
    private String status;

    @Schema(description = "Final balance after transaction")
    private BigDecimal final_balance;

    @Schema(description = "Transaction timestamp (ISO 8601)")
    private String timestamp;
}
