package com.qris.payment.adapter.in.web;

import com.qris.payment.application.dto.request.CreateApiClientRequest;
import com.qris.payment.application.dto.request.UpdateApiClientRequest;
import com.qris.payment.application.dto.request.UpdateTransactionRequest;
import com.qris.payment.application.dto.response.*;
import com.qris.payment.application.usecase.admin.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Operations", description = "Administrative functions for transactions and API clients")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final ListTransactionsUseCase listTransactionsUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final ListApiClientsUseCase listApiClientsUseCase;
    private final CreateApiClientUseCase createApiClientUseCase;
    private final UpdateApiClientUseCase updateApiClientUseCase;

    public AdminController(ListTransactionsUseCase listTransactionsUseCase,
                           UpdateTransactionUseCase updateTransactionUseCase,
                           ListApiClientsUseCase listApiClientsUseCase,
                           CreateApiClientUseCase createApiClientUseCase,
                           UpdateApiClientUseCase updateApiClientUseCase) {
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.updateTransactionUseCase = updateTransactionUseCase;
        this.listApiClientsUseCase = listApiClientsUseCase;
        this.createApiClientUseCase = createApiClientUseCase;
        this.updateApiClientUseCase = updateApiClientUseCase;
    }

    @GetMapping("/transactions")
    @Operation(
            summary = "List Transactions",
            description = "Get all transactions for admin review"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> listTransactions() {
        List<TransactionResponse> transactions = listTransactionsUseCase.execute();
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @PutMapping("/transactions/{transaction_id}")
    @Operation(
            summary = "Update Transaction",
            description = "Update transaction status or amount"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Transaction updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
            @Parameter(description = "Transaction ID")
            @PathVariable("transaction_id") String transactionId,
            @Valid @RequestBody UpdateTransactionRequest request) {

        TransactionResponse response = updateTransactionUseCase.execute(transactionId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/api-clients")
    @Operation(
            summary = "List API Clients",
            description = "Get all configured API clients"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "API clients retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<List<ApiClientResponse>>> listApiClients() {
        List<ApiClientResponse> clients = listApiClientsUseCase.execute();
        return ResponseEntity.ok(ApiResponse.success(clients));
    }

    @PostMapping("/api-clients")
    @Operation(
            summary = "Create API Client",
            description = "Add new API client"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "API client created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error or client already exists",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<ApiClientResponse>> createApiClient(
            @Valid @RequestBody CreateApiClientRequest request) {

        ApiClientResponse response = createApiClientUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/api-clients/{client_id}")
    @Operation(
            summary = "Update API Client",
            description = "Update API client secret or status"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "API client updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "API client not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<ApiClientResponse>> updateApiClient(
            @Parameter(description = "Client ID")
            @PathVariable("client_id") String clientId,
            @Valid @RequestBody UpdateApiClientRequest request) {

        ApiClientResponse response = updateApiClientUseCase.execute(clientId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
