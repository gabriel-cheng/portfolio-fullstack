package com.example.ghcarvalho.portfolio;

import com.example.ghcarvalho.portfolio.project.application.dto.education.EducationRequestDto;
import com.example.ghcarvalho.portfolio.project.infra.persistence.education.EducationJpaRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class EducationIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private EducationJpaRepository educationJpaRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private Cookie adminAuthCookie;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        educationJpaRepository.deleteAll();
        adminUserRepository.deleteAll();

        AdminUserEntity admin = adminUserRepository.save(
                new AdminUserEntity("admin", passwordEncoder.encode("admin123456789"))
        );
        String token = jwtUtil.generateToken(admin.getUsername());
        adminAuthCookie = new Cookie("auth_token", token);
    }

    @Test
    @DisplayName("Should create Education, query via admin and public endpoints matching expected public response")
    void shouldCreateAndRetrieveEducationPublicly() throws Exception {
        EducationRequestDto request = new EducationRequestDto(
                "Análise e Desenvolvimento de Sistemas",
                "Fatec",
                "2020",
                "2023",
                "Concluído",
                "Graduação",
                "Formação com foco em engenharia de software, modelagem de banco de dados, desenvolvimento web, algoritmos e arquitetura de sistemas.",
                List.of(
                        "Java",
                        "Spring Boot",
                        "TypeScript",
                        "React",
                        "PostgreSQL",
                        "Estruturas de Dados"
                ),
                "/images/unavailable.png",
                "Diploma de Graduação em Análise e Desenvolvimento de Sistemas",
                1
        );

        // 1. Create via Admin endpoint
        MvcResult result = mockMvc.perform(post("/api/v1/admin/educations")
                        .with(csrf())
                        .cookie(adminAuthCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.degree").value("Análise e Desenvolvimento de Sistemas"))
                .andExpect(jsonPath("$.institution").value("Fatec"))
                .andExpect(jsonPath("$.startYear").value("2020"))
                .andExpect(jsonPath("$.endYear").value("2023"))
                .andExpect(jsonPath("$.status").value("Concluído"))
                .andExpect(jsonPath("$.type").value("Graduação"))
                .andExpect(jsonPath("$.diplomaImage").value("/images/unavailable.png"))
                .andExpect(jsonPath("$.diplomaAlt").value("Diploma de Graduação em Análise e Desenvolvimento de Sistemas"))
                .andExpect(jsonPath("$.skills", hasSize(6)))
                .andReturn();

        UUID educationId = UUID.fromString(
                objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText()
        );

        // 2. Query via Public endpoint GET /api/v1/public/educations
        mockMvc.perform(get("/api/v1/public/educations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(educationId.toString()))
                .andExpect(jsonPath("$[0].degree").value("Análise e Desenvolvimento de Sistemas"))
                .andExpect(jsonPath("$[0].institution").value("Fatec"))
                .andExpect(jsonPath("$[0].startYear").value("2020"))
                .andExpect(jsonPath("$[0].endYear").value("2023"))
                .andExpect(jsonPath("$[0].status").value("Concluído"))
                .andExpect(jsonPath("$[0].type").value("Graduação"))
                .andExpect(jsonPath("$[0].description").value(
                        "Formação com foco em engenharia de software, modelagem de banco de dados, desenvolvimento web, algoritmos e arquitetura de sistemas."
                ))
                .andExpect(jsonPath("$[0].skills[0]").value("Java"))
                .andExpect(jsonPath("$[0].skills[1]").value("Spring Boot"))
                .andExpect(jsonPath("$[0].skills[2]").value("TypeScript"))
                .andExpect(jsonPath("$[0].skills[3]").value("React"))
                .andExpect(jsonPath("$[0].skills[4]").value("PostgreSQL"))
                .andExpect(jsonPath("$[0].skills[5]").value("Estruturas de Dados"))
                .andExpect(jsonPath("$[0].diplomaImage").value("/images/unavailable.png"))
                .andExpect(jsonPath("$[0].diplomaAlt").value("Diploma de Graduação em Análise e Desenvolvimento de Sistemas"));

        // 3. Query Public by ID
        mockMvc.perform(get("/api/v1/public/educations/" + educationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(educationId.toString()))
                .andExpect(jsonPath("$.degree").value("Análise e Desenvolvimento de Sistemas"));

        // 4. Update via Admin endpoint
        EducationRequestDto updateRequest = new EducationRequestDto(
                "Engenharia de Software",
                "Fatec",
                "2020",
                "2024",
                "Concluído",
                "Graduação",
                "Atualizada",
                List.of("Java", "Kotlin"),
                "/images/diploma-eng.png",
                "Diploma",
                2
        );

        mockMvc.perform(put("/api/v1/admin/educations/" + educationId)
                        .with(csrf())
                        .cookie(adminAuthCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.degree").value("Engenharia de Software"))
                .andExpect(jsonPath("$.endYear").value("2024"));

        // 5. Delete via Admin endpoint
        mockMvc.perform(delete("/api/v1/admin/educations/" + educationId)
                        .with(csrf())
                        .cookie(adminAuthCookie))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/public/educations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        assertThat(educationJpaRepository.findById(educationId)).isEmpty();
    }
}

