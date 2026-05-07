package com.qris.payment.application.usecase.qris;

import com.qris.payment.application.dto.response.InquiryMetadata;
import com.qris.payment.application.dto.response.InquiryResponse;
import com.qris.payment.application.port.out.CachePort;
import com.qris.payment.application.port.out.MerchantRepositoryPort;
import com.qris.payment.domain.entity.Merchant;
import com.qris.payment.infrastructure.qris.QrisParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class QrisInquiryUseCase {

    private static final Logger log = LoggerFactory.getLogger(QrisInquiryUseCase.class);
    private static final String CACHE_PREFIX = "inquiry:";

    private final QrisParser qrisParser;
    private final MerchantRepositoryPort merchantRepositoryPort;
    private final CachePort cachePort;
    private final long inquiryTtlMinutes;

    public QrisInquiryUseCase(QrisParser qrisParser,
                              MerchantRepositoryPort merchantRepositoryPort,
                              CachePort cachePort,
                              @Value("${app.cache.inquiry-ttl-minutes:5}") long inquiryTtlMinutes) {
        this.qrisParser = qrisParser;
        this.merchantRepositoryPort = merchantRepositoryPort;
        this.cachePort = cachePort;
        this.inquiryTtlMinutes = inquiryTtlMinutes;
    }

    public record InquiryResult(InquiryResponse data, InquiryMetadata metadata) {}

    public InquiryResult execute(String qrisPayload) {
        long startTime = System.currentTimeMillis();

        // Check cache first
        String cacheKey = CACHE_PREFIX + qrisPayload.hashCode();
        Optional<InquiryResponse> cached = cachePort.get(cacheKey, InquiryResponse.class);

        if (cached.isPresent()) {
            long latency = System.currentTimeMillis() - startTime;
            InquiryMetadata metadata = InquiryMetadata.builder()
                    .latency_ms(latency)
                    .source("cache")
                    .build();
            return new InquiryResult(cached.get(), metadata);
        }

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

        InquiryResponse response = InquiryResponse.builder()
                .merchant_id(merchantId)
                .merchant_name(merchantName)
                .terminal_id(terminalId)
                .city(city)
                .fixed_amount(fixedAmount)
                .inquiry_id(inquiryId)
                .build();

        // Cache the inquiry response
        cachePort.put(cacheKey, response, Duration.ofMinutes(inquiryTtlMinutes));
        // Also cache by inquiry_id for payment lookup
        cachePort.put("inquiry_data:" + inquiryId, response, Duration.ofMinutes(inquiryTtlMinutes));

        long latency = System.currentTimeMillis() - startTime;
        InquiryMetadata metadata = InquiryMetadata.builder()
                .latency_ms(latency)
                .source("database")
                .build();

        return new InquiryResult(response, metadata);
    }
}
