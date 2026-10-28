package com.example.ghcarvalho.portfolio.project.infra.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret:}")
    private String secret;

    @Value("${app.jwt.expiration:10h}")
    private Duration expiration;

    private SecretKey signingKey;

    @PostConstruct
    void initialize() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "A variável JWT_SECRET precisa estar configurada."
            );
        }

        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);

            if (keyBytes.length < 32) {
                throw new IllegalStateException(
                        "JWT_SECRET deve conter pelo menos 32 bytes."
                );
            }

            signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    "JWT_SECRET deve ser uma chave Base64 válida de pelo menos 32 bytes.",
                    exception
            );
        }

        if (expiration == null || expiration.isZero() || expiration.isNegative()) {
            expiration = Duration.ofHours(10);
        }
    }

    public String generateToken(String username) {
        return generateToken(username, Instant.now());
    }

    public String generateToken(String username, Instant issuedAt) {
        Instant now = issuedAt != null ? issuedAt.truncatedTo(ChronoUnit.SECONDS) : Instant.now().truncatedTo(ChronoUnit.SECONDS);

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Instant extractIssuedAt(String token) {
        Date issuedAt = parseClaims(token).getIssuedAt();
        return issuedAt != null ? issuedAt.toInstant() : null;
    }

    public Instant extractExpiration(String token) {
        Date expirationDate = parseClaims(token).getExpiration();
        return expirationDate != null ? expirationDate.toInstant() : null;
    }

    public boolean isValid(String token) {
        try {
            Claims claims = parseClaims(token);

            return claims.getSubject() != null
                    && !claims.getSubject().isBlank()
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public boolean isIssuedAfterCredentialsChange(Instant issuedAt, Instant credentialsUpdatedAt) {
        if (credentialsUpdatedAt == null) {
            return true;
        }
        if (issuedAt == null) {
            return false;
        }
        return !issuedAt.isBefore(credentialsUpdatedAt.truncatedTo(ChronoUnit.SECONDS));
    }

    public Duration getExpiration() {
        return expiration;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
