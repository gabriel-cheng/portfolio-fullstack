package com.example.ghcarvalho.portfolio.project.infra.controller;

import com.example.ghcarvalho.portfolio.project.application.dto.auth.LoginDto;
import com.example.ghcarvalho.portfolio.project.application.dto.auth.UserResponseDTO;
import com.example.ghcarvalho.portfolio.project.application.exceptions.AuthenticationFailedException;
import com.example.ghcarvalho.portfolio.project.infra.security.AuthenticatedUser;
import com.example.ghcarvalho.portfolio.project.infra.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping({"/auth", "/api/v1/auth"})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final Duration tokenExpiration;
    private final String cookieName;
    private final boolean secureCookie;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            @Value("${app.jwt.expiration:10h}") Duration tokenExpiration,
            @Value("${app.jwt.cookie-name:auth_token}") String cookieName,
            @Value("${app.jwt.cookie-secure:true}") boolean secureCookie
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenExpiration = tokenExpiration;
        this.cookieName = cookieName;
        this.secureCookie = secureCookie;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(
            @RequestBody @Valid LoginDto request
    ) {
        final Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
        } catch (BadCredentialsException | InternalAuthenticationServiceException exception) {
            throw new AuthenticationFailedException("Usuário ou senha inválidos.", exception);
        } catch (AuthenticationException exception) {
            throw new AuthenticationFailedException("Falha na autenticação.", exception);
        }

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        String token = jwtUtil.generateToken(authenticatedUser.getUsername());

        ResponseCookie cookie = ResponseCookie
                .from(cookieName, token)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(tokenExpiration != null ? tokenExpiration : Duration.ofHours(10))
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(UserResponseDTO.from(authenticatedUser));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie expiredCookie = ResponseCookie
                .from(cookieName, "")
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .build();
    }
}
