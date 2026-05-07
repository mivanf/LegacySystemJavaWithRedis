package com.qris.payment.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "QRIS payment request")
public class PaymentRequest {

    @NotBlank(message = "Inquiry ID is required")
    @Schema(description = "Inquiry ID from QRIS inquiry", example = "550e8400-e29b-41d4-a716-446655440000")
    private String inquiry_id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0", inclusive = false, message = "Amount must be greater than 0")
    @Schema(description = "Payment amount", example = "50000")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    @Schema(description = "Payment method", example = "balance", allowableValues = {"balance"})
    private String payment_method;

    @NotBlank(message = "Pincode is required")
    @Size(min = 6, max = 6, message = "Pincode must be exactly 6 digits")
    @Schema(description = "6-digit PIN code", example = "123456")
    private String pincode;
}
