package com.qris.payment.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update API client request")
public class UpdateApiClientRequest {

    @Schema(description = "New client secret", example = "new_secret_key_456")
    private String client_secret;

    @Schema(description = "Updated status", example = "INACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
}
