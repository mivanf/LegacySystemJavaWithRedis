package com.qris.payment.application.usecase.qris;

import com.qris.payment.application.dto.response.MerchantResponse;
import com.qris.payment.application.port.out.MerchantRepositoryPort;
import com.qris.payment.domain.entity.Merchant;
import com.qris.payment.infrastructure.qris.QrisParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Service
public class AddMerchantFromImageUseCase {

    private static final Logger log = LoggerFactory.getLogger(AddMerchantFromImageUseCase.class);

    private final QrisImageInquiryUseCase qrisImageInquiryUseCase;
    private final QrisParser qrisParser;
    private final MerchantRepositoryPort merchantRepositoryPort;

    public AddMerchantFromImageUseCase(QrisImageInquiryUseCase qrisImageInquiryUseCase,
                                       QrisParser qrisParser,
                                       MerchantRepositoryPort merchantRepositoryPort) {
        this.qrisImageInquiryUseCase = qrisImageInquiryUseCase;
        this.qrisParser = qrisParser;
        this.merchantRepositoryPort = merchantRepositoryPort;
    }

    @Transactional
    public MerchantResponse execute(MultipartFile imageFile) {
        // Decode QR image to get payload
        String qrisPayload = qrisImageInquiryUseCase.decodeQrImage(imageFile);

        // Parse QRIS payload
        Map<String, String> parsed = qrisParser.parse(qrisPayload);

        String merchantId = qrisParser.extractMerchantId(parsed);
        String merchantName = qrisParser.extractMerchantName(parsed);
        String city = qrisParser.extractCity(parsed);
        String mcc = qrisParser.extractMcc(parsed);
        String terminalId = qrisParser.extractTerminalId(parsed);

        // Check if merchant already exists
        Optional<Merchant> existing = merchantRepositoryPort.findByMerchantId(merchantId);

        if (existing.isPresent()) {
            // Reactivate existing merchant
            Merchant merchant = existing.get();
            merchant.setIsActive(true);
            merchant.setQrisPayload(qrisPayload);
            merchantRepositoryPort.save(merchant);

            log.info("Reactivated existing merchant: {}", merchantId);

            return MerchantResponse.builder()
                    .merchant_id(merchant.getMerchantId())
                    .merchant_name(merchant.getMerchantName())
                    .city(merchant.getCity())
                    .mcc(merchant.getMcc())
                    .is_active(true)
                    .is_new(false)
                    .build();
        }

        // Create new merchant
        Merchant merchant = Merchant.builder()
                .merchantId(merchantId)
                .merchantName(merchantName)
                .terminalId(terminalId)
                .city(city)
                .mcc(mcc)
                .qrisPayload(qrisPayload)
                .isActive(true)
                .build();

        Merchant saved = merchantRepositoryPort.save(merchant);

        log.info("Created new merchant: {}", merchantId);

        return MerchantResponse.builder()
                .merchant_id(saved.getMerchantId())
                .merchant_name(saved.getMerchantName())
                .city(saved.getCity())
                .mcc(saved.getMcc())
                .is_active(true)
                .is_new(true)
                .build();
    }
}
