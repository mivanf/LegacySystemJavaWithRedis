package com.qris.payment.infrastructure.persistence.adapter;

import com.qris.payment.application.port.out.ApiClientRepositoryPort;
import com.qris.payment.domain.entity.ApiClient;
import com.qris.payment.infrastructure.persistence.repository.JpaApiClientRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ApiClientRepositoryAdapter implements ApiClientRepositoryPort {

    private final JpaApiClientRepository jpaApiClientRepository;

    public ApiClientRepositoryAdapter(JpaApiClientRepository jpaApiClientRepository) {
        this.jpaApiClientRepository = jpaApiClientRepository;
    }

    @Override
    public ApiClient save(ApiClient apiClient) {
        return jpaApiClientRepository.save(apiClient);
    }

    @Override
    public Optional<ApiClient> findByClientId(String clientId) {
        return jpaApiClientRepository.findByClientId(clientId);
    }

    @Override
    public List<ApiClient> findAll() {
        return jpaApiClientRepository.findAll();
    }

    @Override
    public boolean existsByClientId(String clientId) {
        return jpaApiClientRepository.existsByClientId(clientId);
    }
}
