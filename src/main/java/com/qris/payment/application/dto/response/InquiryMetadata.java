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
@Schema(description = "Inquiry response metadata")
public class InquiryMetadata {

    @Schema(description = "Processing latency in milliseconds", example = "45")
    private long latency_ms;

    @Schema(description = "Data source (cache or database)", example = "database")
    private String source;
}
