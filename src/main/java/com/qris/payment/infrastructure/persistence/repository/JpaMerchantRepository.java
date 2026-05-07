package com.qris.payment.infrastructure.persistence.repository;

import com.qris.payment.domain.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaMerchantRepository extends JpaRepository<Merchant, UUID> {

    Optional<Merchant> findByMerchantId(String merchantId);

    boolean existsByMerchantId(String merchantId);
}
