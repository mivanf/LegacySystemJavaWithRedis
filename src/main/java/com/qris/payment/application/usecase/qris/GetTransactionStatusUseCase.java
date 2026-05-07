package com.qris.payment.application.usecase.qris;

import com.qris.payment.application.dto.response.TransactionStatusResponse;
import com.qris.payment.application.port.out.TransactionRepositoryPort;
import com.qris.payment.application.port.out.UserRepositoryPort;
import com.qris.payment.domain.entity.Transaction;
import com.qris.payment.domain.entity.User;
import com.qris.payment.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GetTransactionStatusUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public GetTransactionStatusUseCase(TransactionRepositoryPort transactionRepositoryPort,
                                       UserRepositoryPort userRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    public TransactionStatusResponse execute(String transactionId) {
        Transaction transaction = transactionRepositoryPort.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        // Get current balance
        BigDecimal finalBalance = BigDecimal.ZERO;
        User user = userRepositoryPort.findByAccountId(transaction.getAccountId()).orElse(null);
        if (user != null) {
            finalBalance = user.getBalance();
        }

        return TransactionStatusResponse.builder()
                .transaction_id(transaction.getTransactionId())
                .status(transaction.getStatus().name())
                .final_balance(finalBalance)
                .timestamp(transaction.getUpdatedAt().toString())
                .build();
    }
}
