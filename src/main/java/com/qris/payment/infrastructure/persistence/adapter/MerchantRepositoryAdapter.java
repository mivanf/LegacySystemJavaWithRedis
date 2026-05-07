package com.qris.payment.infrastructure.persistence.adapter;

import com.qris.payment.application.port.out.MerchantRepositoryPort;
import com.qris.payment.domain.entity.Merchant;
import com.qris.payment.infrastructure.persistence.repository.JpaMerchantRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MerchantRepositoryAdapter implements MerchantRepositoryPort {

    private final JpaMerchantRepository jpaMerchantRepository;

    public MerchantRepositoryAdapter(JpaMerchantRepository jpaMerchantRepository) {
        this.jpaMerchantRepository = jpaMerchantRepository;
    }

    @Override
    public Merchant save(Merchant merchant) {
        return jpaMerchantRepository.save(merchant);
    }

    @Override
    public Optional<Merchant> findByMerchantId(String merchantId) {
        return jpaMerchantRepository.findByMerchantId(merchantId);
    }

    @Override
    public boolean existsByMerchantId(String merchantId) {
        return jpaMerchantRepository.existsByMerchantId(merchantId);
    }
}
