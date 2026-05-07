package com.qris.payment.application.port.out;

import com.qris.payment.domain.entity.User;

import java.util.Optional;

/**
 * Output port for user repository operations.
 */
public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByAccountId(String accountId);

    boolean existsByUsername(String username);
}
