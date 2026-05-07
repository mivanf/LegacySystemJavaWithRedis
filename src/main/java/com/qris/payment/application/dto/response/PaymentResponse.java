package com.qris.payment.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment submission response")
public class PaymentResponse {

    @Schema(description = "Payment processing status", example = "PENDING")
    private String status;

    @Schema(description = "Unique transaction identifier")
    private String transaction_id;

    @Schema(description = "Status message", example = "Payment is being processed")
    private String message;

    @Schema(description = "Estimated completion time", example = "2024-01-15T10:30:00Z")
    private String estimated_completion;
}
