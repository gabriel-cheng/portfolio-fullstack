package com.example.ghcarvalho.portfolio.project.infra.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Value("${app.jwt.cookie-name:auth_token}")
    private String cookieName;

    public JwtAuthFilter(
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null
                && SecurityContextHolder.getContext().getAuthentication() == null
                && jwtUtil.isValid(token)) {

            try {
                String username = jwtUtil.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (userDetails instanceof AuthenticatedUser authenticatedUser) {
                    Instant issuedAt = jwtUtil.extractIssuedAt(token);

                    boolean validCredentials = jwtUtil.isIssuedAfterCredentialsChange(
                            issuedAt,
                            authenticatedUser.getCredentialsUpdatedAt()
                    );

                    if (validCredentials
                            && authenticatedUser.isEnabled()
                            && authenticatedUser.isAccountNonLocked()
                            && authenticatedUser.isAccountNonExpired()
                            && authenticatedUser.isCredentialsNonExpired()) {

                        var authentication = new UsernamePasswordAuthenticationToken(
                                authenticatedUser,
                                null,
                                authenticatedUser.getAuthorities()
                        );

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        var context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(authentication);
                        SecurityContextHolder.setContext(context);
                    } else {
                        SecurityContextHolder.clearContext();
                    }
                }
            } catch (JwtException | IllegalArgumentException | UsernameNotFoundException exception) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
