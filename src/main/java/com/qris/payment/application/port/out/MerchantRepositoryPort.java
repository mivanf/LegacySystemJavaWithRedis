package com.qris.payment.application.port.out;

import com.qris.payment.domain.entity.Merchant;

import java.util.Optional;

/**
 * Output port for merchant repository operations.
 */
public interface MerchantRepositoryPort {

    Merchant save(Merchant merchant);

    Optional<Merchant> findByMerchantId(String merchantId);

    boolean existsByMerchantId(String merchantId);
}
