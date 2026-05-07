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
@Schema(description = "Authentication response with JWT token")
public class AuthResponse {

    @Schema(description = "JWT authentication token")
    private String token;

    @Schema(description = "User account ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String account_id;

    @Schema(description = "Account balance", example = "1000000.00")
    private BigDecimal balance;
}
