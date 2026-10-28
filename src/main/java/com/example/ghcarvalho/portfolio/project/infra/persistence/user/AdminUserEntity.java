package com.example.ghcarvalho.portfolio.project.infra.persistence.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(
    name = "admin_users",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_admin_users_username",
            columnNames = "username"
        )
    }
)
public class AdminUserEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "credentials_updated_at", nullable = false)
    private Instant credentialsUpdatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected AdminUserEntity() {
    }

    public AdminUserEntity(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.enabled = true;
    }

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (credentialsUpdatedAt == null) {
            credentialsUpdatedAt = Instant.now();
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public boolean isIssuedAfterCredentialsChange(Instant issuedAt) {
        if (credentialsUpdatedAt == null) {
            return true;
        }
        if (issuedAt == null) {
            return false;
        }
        return !issuedAt.isBefore(credentialsUpdatedAt.truncatedTo(ChronoUnit.SECONDS));
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getCredentialsUpdatedAt() {
        return credentialsUpdatedAt;
    }

    public void setCredentialsUpdatedAt(Instant credentialsUpdatedAt) {
        this.credentialsUpdatedAt = credentialsUpdatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
