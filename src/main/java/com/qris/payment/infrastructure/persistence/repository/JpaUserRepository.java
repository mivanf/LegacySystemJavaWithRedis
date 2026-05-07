package com.qris.payment.infrastructure.persistence.repository;

import com.qris.payment.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByAccountId(String accountId);

    boolean existsByUsername(String username);
}
