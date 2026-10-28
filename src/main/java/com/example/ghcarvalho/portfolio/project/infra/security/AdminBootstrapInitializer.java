package com.example.ghcarvalho.portfolio.project.infra.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.ghcarvalho.portfolio.project.infra.persistence.user.AdminUserEntity;
import com.example.ghcarvalho.portfolio.project.infra.persistence.user.AdminUserRepository;

@Component
public class AdminBootstrapInitializer implements ApplicationRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminBootstrapInitializer(
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.username:}") String username,
            @Value("${app.bootstrap-admin.password:}") String password
    ) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        boolean usernameProvided = username != null && !username.isBlank();
        boolean passwordProvided = password != null && !password.isBlank();

        if (!usernameProvided && !passwordProvided) {
            return;
        }

        if (!usernameProvided || !passwordProvided) {
            throw new IllegalStateException(
                    "Configure ADMIN_USERNAME e ADMIN_PASSWORD juntas."
            );
        }

        String normalizedUsername = username.trim();

        if (normalizedUsername.length() > 100) {
            throw new IllegalStateException(
                    "ADMIN_USERNAME deve ter no máximo 100 caracteres."
            );
        }

        if (password.length() < 12) {
            throw new IllegalStateException(
                    "ADMIN_PASSWORD deve ter pelo menos 12 caracteres."
            );
        }

        if (adminUserRepository.existsByUsername(normalizedUsername)) {
            return;
        }

        AdminUserEntity admin = new AdminUserEntity(
                normalizedUsername,
                passwordEncoder.encode(password)
        );

        adminUserRepository.save(admin);
    }
}
