package com.qris.payment.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "QRIS inquiry response with merchant data")
public class InquiryResponse implements Serializable {

    @Schema(description = "Merchant identifier")
    private String merchant_id;

    @Schema(description = "Merchant name")
    private String merchant_name;

    @Schema(description = "Terminal identifier")
    private String terminal_id;

    @Schema(description = "Merchant city")
    private String city;

    @Schema(description = "Fixed payment amount (0 if dynamic)")
    private BigDecimal fixed_amount;

    @Schema(description = "Unique inquiry identifier for payment")
    private String inquiry_id;
}
