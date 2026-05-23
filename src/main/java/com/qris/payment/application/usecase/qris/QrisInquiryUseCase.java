package com.qris.payment.application.usecase.qris;

import com.qris.payment.application.dto.response.InquiryMetadata;
import com.qris.payment.application.dto.response.InquiryResponse;
import com.qris.payment.application.port.out.InquiryRepositoryPort;
import com.qris.payment.application.port.out.MerchantRepositoryPort;
import com.qris.payment.domain.entity.Inquiry;
import com.qris.payment.domain.entity.Merchant;
import com.qris.payment.infrastructure.qris.QrisParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class QrisInquiryUseCase {

    private static final Logger log = LoggerFactory.getLogger(QrisInquiryUseCase.class);

    private final QrisParser qrisParser;
    private final MerchantRepositoryPort merchantRepositoryPort;
    private final InquiryRepositoryPort inquiryRepositoryPort;

    public QrisInquiryUseCase(QrisParser qrisParser,
                              MerchantRepositoryPort merchantRepositoryPort,
                              InquiryRepositoryPort inquiryRepositoryPort) {
        this.qrisParser = qrisParser;
        this.merchantRepositoryPort = merchantRepositoryPort;
        this.inquiryRepositoryPort = inquiryRepositoryPort;
    }

    public record InquiryResult(InquiryResponse data, InquiryMetadata metadata) {}

    @Transactional
    public InquiryResult execute(String qrisPayload) {
        long startTime = System.currentTimeMillis();

        // Parse QRIS payload
        Map<String, String> parsed = qrisParser.parse(qrisPayload);

        String merchantId = qrisParser.extractMerchantId(parsed);
        String merchantName = qrisParser.extractMerchantName(parsed);
        String terminalId = qrisParser.extractTerminalId(parsed);
        String city = qrisParser.extractCity(parsed);
        BigDecimal fixedAmount = qrisParser.extractAmount(parsed);

        // Try to find merchant in database for additional data
        Optional<Merchant> existingMerchant = merchantRepositoryPort.findByMerchantId(merchantId);
        if (existingMerchant.isPresent()) {
            Merchant m = existingMerchant.get();
            merchantName = m.getMerchantName();
            city = m.getCity();
            if (terminalId == null) terminalId = m.getTerminalId();
            if (fixedAmount.compareTo(BigDecimal.ZERO) == 0 && m.getFixedAmount() != null) {
                fixedAmount = m.getFixedAmount();
            }
        }

        String inquiryId = UUID.randomUUID().toString();

        // Save inquiry to database (no caching)
        Inquiry inquiry = Inquiry.builder()
                .inquiryId(inquiryId)
                .merchantId(merchantId)
                .merchantName(merchantName)
                .terminalId(terminalId)
                .city(city)
                .fixedAmount(fixedAmount)
                .build();
        inquiryRepositoryPort.save(inquiry);

        InquiryResponse response = InquiryResponse.builder()
                .merchant_id(merchantId)
                .merchant_name(merchantName)
                .terminal_id(terminalId)
                .city(city)
                .fixed_amount(fixedAmount)
                .inquiry_id(inquiryId)
                .build();

        long latency = System.currentTimeMillis() - startTime;
        InquiryMetadata metadata = InquiryMetadata.builder()
                .latency_ms(latency)
                .source("database")
                .build();

        return new InquiryResult(response, metadata);
    }
}
