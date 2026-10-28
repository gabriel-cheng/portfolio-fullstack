package com.example.ghcarvalho.portfolio.project.infra.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminUserRepository
        extends JpaRepository<AdminUserEntity, UUID> {

    Optional<AdminUserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}