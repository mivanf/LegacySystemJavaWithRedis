package com.qris.payment.application.usecase.auth;

import com.qris.payment.application.dto.request.LoginRequest;
import com.qris.payment.application.dto.response.AuthResponse;
import com.qris.payment.application.port.out.UserRepositoryPort;
import com.qris.payment.domain.entity.User;
import com.qris.payment.infrastructure.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginUseCase(UserRepositoryPort userRepositoryPort,
                        PasswordEncoder passwordEncoder,
                        JwtTokenProvider jwtTokenProvider) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse execute(LoginRequest request) {
        User user = userRepositoryPort.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .account_id(user.getAccountId())
                .balance(user.getBalance())
                .build();
    }
}
