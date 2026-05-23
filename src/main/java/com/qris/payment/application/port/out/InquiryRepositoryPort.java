package com.qris.payment.application.port.out;

import com.qris.payment.domain.entity.Inquiry;

import java.util.Optional;

/**
 * Output port for inquiry repository operations.
 */
public interface InquiryRepositoryPort {

    Inquiry save(Inquiry inquiry);

    Optional<Inquiry> findByInquiryId(String inquiryId);
}
