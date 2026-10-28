package com.example.ghcarvalho.portfolio.project.infra.controller;

import com.example.ghcarvalho.portfolio.project.application.dto.auth.UserResponseDTO;
import com.example.ghcarvalho.portfolio.project.application.dto.user.CreateUserDTO;
import com.example.ghcarvalho.portfolio.project.application.dto.user.UpdatePasswordDTO;
import com.example.ghcarvalho.portfolio.project.application.exceptions.AuthenticationFailedException;
import com.example.ghcarvalho.portfolio.project.application.exceptions.UserAlreadyExistsException;
import com.example.ghcarvalho.portfolio.project.infra.persistence.user.AdminUserEntity;
import com.example.ghcarvalho.portfolio.project.infra.persistence.user.AdminUserRepository;
import com.example.ghcarvalho.portfolio.project.infra.security.AuthenticatedUser;
import com.example.ghcarvalho.portfolio.project.infra.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping({"/users", "/api/v1/users"})
public class UserController {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final Duration tokenExpiration;
    private final String cookieName;
    private final boolean secureCookie;

    public UserController(
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            @Value("${app.jwt.expiration:10h}") Duration tokenExpiration,
            @Value("${app.jwt.cookie-name:auth_token}") String cookieName,
            @Value("${app.jwt.cookie-secure:true}") boolean secureCookie
    ) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenExpiration = tokenExpiration;
        this.cookieName = cookieName;
        this.secureCookie = secureCookie;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid CreateUserDTO request) {
        String normalizedUsername = request.username().trim();

        if (adminUserRepository.existsByUsername(normalizedUsername)) {
            throw new UserAlreadyExistsException("Nome de usuário já cadastrado.");
        }

        AdminUserEntity admin = new AdminUserEntity(
                normalizedUsername,
                passwordEncoder.encode(request.password())
        );

        AdminUserEntity savedAdmin = adminUserRepository.save(admin);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponseDTO.from(savedAdmin));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        if (authenticatedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return adminUserRepository.findById(authenticatedUser.getId())
                .map(UserResponseDTO::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/me/password")
    @Transactional
    public ResponseEntity<UserResponseDTO> updatePassword(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody @Valid UpdatePasswordDTO request
    ) {
        if (authenticatedUser == null) {
            throw new AuthenticationFailedException("Usuário não autenticado.");
        }

        AdminUserEntity admin = adminUserRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new AuthenticationFailedException("Usuário não encontrado."));

        if (!passwordEncoder.matches(request.currentPassword(), admin.getPasswordHash())) {
            throw new AuthenticationFailedException("Senha atual incorreta.");
        }

        admin.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        admin.setCredentialsUpdatedAt(Instant.now());
        AdminUserEntity updatedAdmin = adminUserRepository.save(admin);

        String newToken = jwtUtil.generateToken(updatedAdmin.getUsername());

        ResponseCookie cookie = ResponseCookie
                .from(cookieName, newToken)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(tokenExpiration != null ? tokenExpiration : Duration.ofHours(10))
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(UserResponseDTO.from(updatedAdmin));
    }
}
