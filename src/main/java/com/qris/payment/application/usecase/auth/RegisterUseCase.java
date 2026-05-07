package com.qris.payment.application.usecase.auth;

import com.qris.payment.application.dto.request.RegisterRequest;
import com.qris.payment.application.dto.response.AuthResponse;
import com.qris.payment.application.port.out.UserRepositoryPort;
import com.qris.payment.domain.entity.User;
import com.qris.payment.infrastructure.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RegisterUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public RegisterUseCase(UserRepositoryPort userRepositoryPort,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthResponse execute(RegisterRequest request) {
        if (userRepositoryPort.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        String accountId = UUID.randomUUID().toString();

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .accountId(accountId)
                .balance(request.getInitial_balance())
                .pincode(passwordEncoder.encode("123456")) // Default pincode
                .build();

        User savedUser = userRepositoryPort.save(user);

        String token = jwtTokenProvider.generateToken(savedUser.getUsername());

        return AuthResponse.builder()
                .token(token)
                .account_id(savedUser.getAccountId())
                .balance(savedUser.getBalance())
                .build();
    }
}
