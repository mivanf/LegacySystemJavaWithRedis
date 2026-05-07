package com.qris.payment.adapter.in.web;

import com.qris.payment.application.dto.request.PaymentRequest;
import com.qris.payment.application.dto.response.*;
import com.qris.payment.application.usecase.qris.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/qris")
@Tag(name = "QRIS Operations", description = "QRIS inquiry, payment, and transaction management")
@SecurityRequirement(name = "Bearer Authentication")
public class QrisController {

    private final QrisInquiryUseCase qrisInquiryUseCase;
    private final QrisImageInquiryUseCase qrisImageInquiryUseCase;
    private final AddMerchantFromImageUseCase addMerchantFromImageUseCase;
    private final CreatePaymentUseCase createPaymentUseCase;
    private final GetTransactionStatusUseCase getTransactionStatusUseCase;

    public QrisController(QrisInquiryUseCase qrisInquiryUseCase,
                          QrisImageInquiryUseCase qrisImageInquiryUseCase,
                          AddMerchantFromImageUseCase addMerchantFromImageUseCase,
                          CreatePaymentUseCase createPaymentUseCase,
                          GetTransactionStatusUseCase getTransactionStatusUseCase) {
        this.qrisInquiryUseCase = qrisInquiryUseCase;
        this.qrisImageInquiryUseCase = qrisImageInquiryUseCase;
        this.addMerchantFromImageUseCase = addMerchantFromImageUseCase;
        this.createPaymentUseCase = createPaymentUseCase;
        this.getTransactionStatusUseCase = getTransactionStatusUseCase;
    }

    @GetMapping("/inquiry/{qris_payload}")
    @Operation(
            summary = "QRIS Inquiry",
            description = "Get merchant data from QRIS payload. The payload can be URL-encoded."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Inquiry successful",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid QRIS payload",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<InquiryResponse>> inquiry(
            @Parameter(description = "QRIS payload string (can be URL-encoded)")
            @PathVariable("qris_payload") String qrisPayload) {

        QrisInquiryUseCase.InquiryResult result = qrisInquiryUseCase.execute(qrisPayload);
        return ResponseEntity.ok(ApiResponse.successWithMetadata(result.data(), result.metadata()));
    }

    @PostMapping(value = "/inquiry/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "QRIS Inquiry from Image",
            description = "Get merchant data from QRIS QR code image"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Inquiry successful",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid image or no QR code found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<InquiryResponse>> inquiryFromImage(
            @Parameter(description = "QR code image file")
            @RequestParam("image") MultipartFile image) {

        QrisInquiryUseCase.InquiryResult result = qrisImageInquiryUseCase.execute(image);
        return ResponseEntity.ok(ApiResponse.successWithMetadata(result.data(), result.metadata()));
    }

    @PostMapping(value = "/merchant/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Add Merchant from Image",
            description = "Create or reactivate a merchant from QRIS QR code image"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Merchant created or reactivated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid image or no QR code found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<MerchantResponse>> addMerchantFromImage(
            @Parameter(description = "QR code image file")
            @RequestParam("image") MultipartFile image) {

        MerchantResponse response = addMerchantFromImageUseCase.execute(image);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/payment")
    @Operation(
            summary = "Create Payment",
            description = "Submit payment for QRIS transaction. Payment is processed asynchronously."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "202",
                    description = "Payment accepted for processing",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error, insufficient balance, or invalid PIN",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Inquiry not found or expired",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        PaymentResponse response = createPaymentUseCase.execute(request, username);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(response));
    }

    @GetMapping("/status/{transaction_id}")
    @Operation(
            summary = "Get Transaction Status",
            description = "Get current status of a transaction"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Transaction status retrieved",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<TransactionStatusResponse>> getTransactionStatus(
            @Parameter(description = "Transaction ID")
            @PathVariable("transaction_id") String transactionId) {

        TransactionStatusResponse response = getTransactionStatusUseCase.execute(transactionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
