package com.qris.payment.application.usecase.qris;

import com.qris.payment.application.dto.request.PaymentRequest;
import com.qris.payment.application.dto.response.PaymentResponse;
import com.qris.payment.application.port.out.InquiryRepositoryPort;
import com.qris.payment.application.port.out.TransactionRepositoryPort;
import com.qris.payment.application.port.out.UserRepositoryPort;
import com.qris.payment.domain.entity.Inquiry;
import com.qris.payment.domain.entity.Transaction;
import com.qris.payment.domain.entity.User;
import com.qris.payment.domain.enums.TransactionStatus;
import com.qris.payment.exception.InsufficientBalanceException;
import com.qris.payment.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class CreatePaymentUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreatePaymentUseCase.class);

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final InquiryRepositoryPort inquiryRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public CreatePaymentUseCase(TransactionRepositoryPort transactionRepositoryPort,
                                UserRepositoryPort userRepositoryPort,
                                InquiryRepositoryPort inquiryRepositoryPort,
                                PasswordEncoder passwordEncoder) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.inquiryRepositoryPort = inquiryRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public PaymentResponse execute(PaymentRequest request, String username) {
        // Validate inquiry from database (no cache)
        Inquiry inquiry = inquiryRepositoryPort.findByInquiryId(request.getInquiry_id())
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found or expired. Please perform a new inquiry."));

        // Get user and validate pincode
        User user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPincode(), user.getPincode())) {
            throw new IllegalArgumentException("Invalid PIN code");
        }

        // Check balance
        BigDecimal amount = request.getAmount();
        if (user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance. Current balance: " + user.getBalance());
        }

        // Deduct balance
        user.setBalance(user.getBalance().subtract(amount));
        userRepositoryPort.save(user);

        // Create transaction
        String transactionId = UUID.randomUUID().toString();
        String traceId = UUID.randomUUID().toString();

        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .traceId(traceId)
                .accountId(user.getAccountId())
                .merchantId(inquiry.getMerchantId())
                .inquiryId(request.getInquiry_id())
                .amount(amount)
                .status(TransactionStatus.PENDING)
                .paymentMethod(request.getPayment_method())
                .build();

        transactionRepositoryPort.save(transaction);

        // Trigger async processing
        processPaymentAsync(transactionId);

        String estimatedCompletion = Instant.now()
                .plus(30, ChronoUnit.SECONDS)
                .toString();

        return PaymentResponse.builder()
                .status("PENDING")
                .transaction_id(transactionId)
                .message("Payment is being processed")
                .estimated_completion(estimatedCompletion)
                .build();
    }

    @Async("paymentExecutor")
    public void processPaymentAsync(String transactionId) {
        try {
            // Simulate payment processing delay
            Thread.sleep(2000);

            Transaction transaction = transactionRepositoryPort.findByTransactionId(transactionId)
                    .orElse(null);

            if (transaction != null && transaction.getStatus() == TransactionStatus.PENDING) {
                transaction.setStatus(TransactionStatus.SUCCESS);
                transactionRepositoryPort.save(transaction);
                log.info("Payment processed successfully: {}", transactionId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment processing interrupted for: {}", transactionId);
        } catch (Exception e) {
            log.error("Payment processing failed for: {}. Error: {}", transactionId, e.getMessage());
            // Mark as failed
            transactionRepositoryPort.findByTransactionId(transactionId).ifPresent(tx -> {
                tx.setStatus(TransactionStatus.FAILED);
                transactionRepositoryPort.save(tx);
            });
        }
    }
}
