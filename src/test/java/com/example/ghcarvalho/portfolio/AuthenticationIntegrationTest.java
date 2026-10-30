package com.example.ghcarvalho.portfolio;

import com.example.ghcarvalho.portfolio.project.application.dto.auth.LoginDto;
import com.example.ghcarvalho.portfolio.project.application.dto.user.CreateUserDto;
import com.example.ghcarvalho.portfolio.project.application.dto.user.UpdatePasswordDto;
import com.example.ghcarvalho.portfolio.project.infra.persistence.user.AdminUserEntity;
import com.example.ghcarvalho.portfolio.project.infra.persistence.user.AdminUserRepository;
import com.example.ghcarvalho.portfolio.project.infra.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthenticationIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        adminUserRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /users - should register a new admin user successfully")
    void shouldRegisterNewUser() throws Exception {
        CreateUserDto request = new CreateUserDto("alice", "password123");

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        assertThat(adminUserRepository.existsByUsername("alice")).isTrue();
    }

    @Test
    @DisplayName("POST /users - duplicate username should return 409 Conflict")
    void shouldRejectDuplicateUsername() throws Exception {
        adminUserRepository.save(new AdminUserEntity("bob", passwordEncoder.encode("secret123")));

        CreateUserDto request = new CreateUserDto("bob", "password456");

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /auth/login - valid credentials returns 200, UserResponseDTO and HttpOnly auth_token cookie")
    void shouldLoginSuccessfullyAndSetCookie() throws Exception {
        adminUserRepository.save(new AdminUserEntity("carol", passwordEncoder.encode("mypassword")));

        LoginDto loginRequest = new LoginDto("carol", "mypassword");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("carol"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andReturn();

        String setCookie = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertThat(setCookie).isNotNull();
        assertThat(setCookie).contains("auth_token=");
        assertThat(setCookie).contains("HttpOnly");
        assertThat(setCookie).contains("SameSite=Strict");
        assertThat(setCookie).contains("Max-Age=36000"); // 10 hours
    }

    @Test
    @DisplayName("POST /auth/login - invalid credentials throws AuthenticationFailedException and returns 401")
    void shouldFailLoginWithBadCredentials() throws Exception {
        adminUserRepository.save(new AdminUserEntity("dave", passwordEncoder.encode("correctpass")));

        LoginDto badPasswordRequest = new LoginDto("dave", "wrongpass");

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badPasswordRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("JwtAuthFilter - request with valid auth_token cookie accesses protected route /users/me")
    void shouldAccessProtectedRouteWithAuthCookie() throws Exception {
        AdminUserEntity admin = adminUserRepository.save(new AdminUserEntity("eve", passwordEncoder.encode("pass123")));
        String token = jwtUtil.generateToken("eve");

        Cookie authCookie = new Cookie("auth_token", token);

        mockMvc.perform(get("/users/me").cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(admin.getId().toString()))
                .andExpect(jsonPath("$.username").value("eve"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("JwtAuthFilter - request without token is rejected with 401/403")
    void shouldRejectAccessWithoutCookie() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("isIssuedAfterCredentialsChange - token issued before password change is immediately invalidated")
    void shouldInvalidateOldTokenAfterPasswordChange() throws Exception {
        AdminUserEntity admin = new AdminUserEntity("frank", passwordEncoder.encode("oldPassword123"));
        admin.setCredentialsUpdatedAt(Instant.now().minus(1, ChronoUnit.HOURS));
        admin = adminUserRepository.save(admin);

        // Generate token before password change (issued 5 minutes ago)
        String oldToken = jwtUtil.generateToken("frank", Instant.now().minus(5, ChronoUnit.MINUTES));

        // Verify old token works initially
        mockMvc.perform(get("/users/me").cookie(new Cookie("auth_token", oldToken)))
                .andExpect(status().isOk());

        // Update password via PUT /users/me/password
        UpdatePasswordDto updatePasswordDTO = new UpdatePasswordDto("oldPassword123", "newPassword456");
        MvcResult updateResult = mockMvc.perform(put("/users/me/password")
                        .with(csrf())
                        .cookie(new Cookie("auth_token", oldToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andReturn();

        // Extract newly reissued cookie
        String newSetCookie = updateResult.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertThat(newSetCookie).contains("auth_token=");
        String newToken = newSetCookie.split(";")[0].replace("auth_token=", "").trim();

        // Old token must now be rejected because admin.credentialsUpdatedAt > oldToken.issuedAt
        mockMvc.perform(get("/users/me").cookie(new Cookie("auth_token", oldToken)))
                .andExpect(status().isForbidden());

        // New token must work
        mockMvc.perform(get("/users/me").cookie(new Cookie("auth_token", newToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("frank"));
    }

    @Test
    @DisplayName("POST /auth/logout - clears auth_token cookie with maxAge=0")
    void shouldLogoutAndClearCookie() throws Exception {
        adminUserRepository.save(new AdminUserEntity("grace", passwordEncoder.encode("pass123")));
        String token = jwtUtil.generateToken("grace");

        MvcResult result = mockMvc.perform(post("/auth/logout")
                        .with(csrf())
                        .cookie(new Cookie("auth_token", token)))
                .andExpect(status().isNoContent())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andReturn();

        String setCookie = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertThat(setCookie).isNotNull();
        assertThat(setCookie).contains("auth_token=");
        assertThat(setCookie).contains("Max-Age=0");
    }
}
