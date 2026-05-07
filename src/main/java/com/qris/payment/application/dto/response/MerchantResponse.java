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
@Schema(description = "Merchant creation/reactivation response")
public class MerchantResponse {

    @Schema(description = "Merchant identifier")
    private String merchant_id;

    @Schema(description = "Merchant name")
    private String merchant_name;

    @Schema(description = "Merchant city")
    private String city;

    @Schema(description = "Merchant Category Code")
    private String mcc;

    @Schema(description = "Whether merchant is active")
    private Boolean is_active;

    @Schema(description = "Whether merchant was newly created")
    private Boolean is_new;
}
