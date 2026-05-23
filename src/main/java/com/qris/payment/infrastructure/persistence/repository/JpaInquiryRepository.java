package com.qris.payment.infrastructure.persistence.repository;

import com.qris.payment.domain.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaInquiryRepository extends JpaRepository<Inquiry, UUID> {

    Optional<Inquiry> findByInquiryId(String inquiryId);
}
