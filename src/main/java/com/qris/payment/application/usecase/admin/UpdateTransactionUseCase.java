package com.qris.payment.application.usecase.admin;

import com.qris.payment.application.dto.request.UpdateTransactionRequest;
import com.qris.payment.application.dto.response.TransactionResponse;
import com.qris.payment.application.port.out.TransactionRepositoryPort;
import com.qris.payment.domain.entity.Transaction;
import com.qris.payment.domain.enums.TransactionStatus;
import com.qris.payment.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateTransactionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;

    public UpdateTransactionUseCase(TransactionRepositoryPort transactionRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Transactional
    public TransactionResponse execute(String transactionId, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepositoryPort.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }

        if (request.getStatus() != null) {
            try {
                transaction.setStatus(TransactionStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + request.getStatus()
                        + ". Must be PENDING, SUCCESS, or FAILED");
            }
        }

        Transaction saved = transactionRepositoryPort.save(transaction);

        return TransactionResponse.builder()
                .transaction_id(saved.getTransactionId())
                .trace_id(saved.getTraceId())
                .account_id(saved.getAccountId())
                .merchant_id(saved.getMerchantId())
                .amount(saved.getAmount())
                .status(saved.getStatus().name())
                .created_at(saved.getCreatedAt().toString())
                .updated_at(saved.getUpdatedAt().toString())
                .build();
    }
}
