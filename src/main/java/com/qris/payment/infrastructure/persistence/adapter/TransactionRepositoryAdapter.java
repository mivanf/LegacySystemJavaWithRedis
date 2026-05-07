package com.qris.payment.infrastructure.persistence.adapter;

import com.qris.payment.application.port.out.TransactionRepositoryPort;
import com.qris.payment.domain.entity.Transaction;
import com.qris.payment.infrastructure.persistence.repository.JpaTransactionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final JpaTransactionRepository jpaTransactionRepository;

    public TransactionRepositoryAdapter(JpaTransactionRepository jpaTransactionRepository) {
        this.jpaTransactionRepository = jpaTransactionRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return jpaTransactionRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> findByTransactionId(String transactionId) {
        return jpaTransactionRepository.findByTransactionId(transactionId);
    }

    @Override
    public List<Transaction> findAll() {
        return jpaTransactionRepository.findAll();
    }
}
