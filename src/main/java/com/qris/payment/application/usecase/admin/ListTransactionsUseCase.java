package com.qris.payment.application.usecase.admin;

import com.qris.payment.application.dto.response.TransactionResponse;
import com.qris.payment.application.port.out.TransactionRepositoryPort;
import com.qris.payment.domain.entity.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListTransactionsUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;

    public ListTransactionsUseCase(TransactionRepositoryPort transactionRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    public List<TransactionResponse> execute() {
        return transactionRepositoryPort.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse toResponse(Transaction tx) {
        return TransactionResponse.builder()
                .transaction_id(tx.getTransactionId())
                .trace_id(tx.getTraceId())
                .account_id(tx.getAccountId())
                .merchant_id(tx.getMerchantId())
                .amount(tx.getAmount())
                .status(tx.getStatus().name())
                .created_at(tx.getCreatedAt().toString())
                .updated_at(tx.getUpdatedAt().toString())
                .build();
    }
}
