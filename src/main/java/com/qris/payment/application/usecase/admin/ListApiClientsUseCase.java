package com.qris.payment.application.usecase.admin;

import com.qris.payment.application.dto.response.ApiClientResponse;
import com.qris.payment.application.port.out.ApiClientRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListApiClientsUseCase {

    private final ApiClientRepositoryPort apiClientRepositoryPort;

    public ListApiClientsUseCase(ApiClientRepositoryPort apiClientRepositoryPort) {
        this.apiClientRepositoryPort = apiClientRepositoryPort;
    }

    public List<ApiClientResponse> execute() {
        return apiClientRepositoryPort.findAll().stream()
                .map(client -> ApiClientResponse.builder()
                        .client_id(client.getClientId())
                        .status(client.getStatus().name())
                        .created_at(client.getCreatedAt().toString())
                        .build())
                .collect(Collectors.toList());
    }
}
