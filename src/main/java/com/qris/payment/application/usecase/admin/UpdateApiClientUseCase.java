package com.qris.payment.application.usecase.admin;

import com.qris.payment.application.dto.request.UpdateApiClientRequest;
import com.qris.payment.application.dto.response.ApiClientResponse;
import com.qris.payment.application.port.out.ApiClientRepositoryPort;
import com.qris.payment.domain.entity.ApiClient;
import com.qris.payment.domain.enums.ApiClientStatus;
import com.qris.payment.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateApiClientUseCase {

    private final ApiClientRepositoryPort apiClientRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public UpdateApiClientUseCase(ApiClientRepositoryPort apiClientRepositoryPort,
                                   PasswordEncoder passwordEncoder) {
        this.apiClientRepositoryPort = apiClientRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ApiClientResponse execute(String clientId, UpdateApiClientRequest request) {
        ApiClient apiClient = apiClientRepositoryPort.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("API client not found: " + clientId));

        if (request.getClient_secret() != null && !request.getClient_secret().isBlank()) {
            apiClient.setClientSecretHash(passwordEncoder.encode(request.getClient_secret()));
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                apiClient.setStatus(ApiClientStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + request.getStatus()
                        + ". Must be ACTIVE or INACTIVE");
            }
        }

        ApiClient saved = apiClientRepositoryPort.save(apiClient);

        return ApiClientResponse.builder()
                .client_id(saved.getClientId())
                .status(saved.getStatus().name())
                .created_at(saved.getCreatedAt().toString())
                .build();
    }
}
