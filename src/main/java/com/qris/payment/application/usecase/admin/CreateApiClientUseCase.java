package com.qris.payment.application.usecase.admin;

import com.qris.payment.application.dto.request.CreateApiClientRequest;
import com.qris.payment.application.dto.response.ApiClientResponse;
import com.qris.payment.application.port.out.ApiClientRepositoryPort;
import com.qris.payment.domain.entity.ApiClient;
import com.qris.payment.domain.enums.ApiClientStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateApiClientUseCase {

    private final ApiClientRepositoryPort apiClientRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public CreateApiClientUseCase(ApiClientRepositoryPort apiClientRepositoryPort,
                                   PasswordEncoder passwordEncoder) {
        this.apiClientRepositoryPort = apiClientRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ApiClientResponse execute(CreateApiClientRequest request) {
        if (apiClientRepositoryPort.existsByClientId(request.getClient_id())) {
            throw new IllegalArgumentException("API client with ID '" + request.getClient_id() + "' already exists");
        }

        ApiClientStatus status;
        try {
            status = ApiClientStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + request.getStatus()
                    + ". Must be ACTIVE or INACTIVE");
        }

        ApiClient apiClient = ApiClient.builder()
                .clientId(request.getClient_id())
                .clientSecretHash(passwordEncoder.encode(request.getClient_secret()))
                .status(status)
                .build();

        ApiClient saved = apiClientRepositoryPort.save(apiClient);

        return ApiClientResponse.builder()
                .client_id(saved.getClientId())
                .status(saved.getStatus().name())
                .created_at(saved.getCreatedAt().toString())
                .build();
    }
}
