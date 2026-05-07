package com.qris.payment.application.port.out;

import com.qris.payment.domain.entity.ApiClient;

import java.util.List;
import java.util.Optional;

/**
 * Output port for API client repository operations.
 */
public interface ApiClientRepositoryPort {

    ApiClient save(ApiClient apiClient);

    Optional<ApiClient> findByClientId(String clientId);

    List<ApiClient> findAll();

    boolean existsByClientId(String clientId);
}
