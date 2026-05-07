package com.qris.payment.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create API client request")
public class CreateApiClientRequest {

    @NotBlank(message = "Client ID is required")
    @Schema(description = "Client identifier", example = "mobile-app-v1")
    private String client_id;

    @NotBlank(message = "Client secret is required")
    @Schema(description = "Client secret", example = "super_secret_key_123")
    private String client_secret;

    @NotBlank(message = "Status is required")
    @Schema(description = "Client status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
}
