package com.qris.payment.application.port.out;

import com.qris.payment.domain.entity.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Output port for transaction repository operations.
 */
public interface TransactionRepositoryPort {

    Transaction save(Transaction transaction);

    Optional<Transaction> findByTransactionId(String transactionId);

    List<Transaction> findAll();
}
