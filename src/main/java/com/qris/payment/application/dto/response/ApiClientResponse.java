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
@Schema(description = "API client response")
public class ApiClientResponse {

    @Schema(description = "Client identifier")
    private String client_id;

    @Schema(description = "Client status")
    private String status;

    @Schema(description = "Creation timestamp (ISO 8601)")
    private String created_at;
}
