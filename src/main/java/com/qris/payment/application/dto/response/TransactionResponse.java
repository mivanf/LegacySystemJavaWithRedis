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
@Schema(description = "Admin transaction response")
public class TransactionResponse {

    @Schema(description = "Transaction identifier")
    private String transaction_id;

    @Schema(description = "Trace identifier for debugging")
    private String trace_id;

    @Schema(description = "User account identifier")
    private String account_id;

    @Schema(description = "Merchant identifier")
    private String merchant_id;

    @Schema(description = "Transaction amount")
    private BigDecimal amount;

    @Schema(description = "Transaction status")
    private String status;

    @Schema(description = "Creation timestamp (ISO 8601)")
    private String created_at;

    @Schema(description = "Last update timestamp (ISO 8601)")
    private String updated_at;
}
