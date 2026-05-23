package com.qris.payment.infrastructure.persistence.adapter;

import com.qris.payment.application.port.out.InquiryRepositoryPort;
import com.qris.payment.domain.entity.Inquiry;
import com.qris.payment.infrastructure.persistence.repository.JpaInquiryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InquiryRepositoryAdapter implements InquiryRepositoryPort {

    private final JpaInquiryRepository jpaInquiryRepository;

    public InquiryRepositoryAdapter(JpaInquiryRepository jpaInquiryRepository) {
        this.jpaInquiryRepository = jpaInquiryRepository;
    }

    @Override
    public Inquiry save(Inquiry inquiry) {
        return jpaInquiryRepository.save(inquiry);
    }

    @Override
    public Optional<Inquiry> findByInquiryId(String inquiryId) {
        return jpaInquiryRepository.findByInquiryId(inquiryId);
    }
}
