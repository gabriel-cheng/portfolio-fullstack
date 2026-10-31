package com.example.ghcarvalho.portfolio;

import com.example.ghcarvalho.portfolio.project.application.dto.company.CompanyRequestDto;
import com.example.ghcarvalho.portfolio.project.application.dto.experience.ExperienceRequestDto;
import com.example.ghcarvalho.portfolio.project.domain.company.PublicationStatus;
import com.example.ghcarvalho.portfolio.project.domain.experience.EmploymentType;
import com.example.ghcarvalho.portfolio.project.infra.persistence.company.CompanyJpaRepository;
import com.example.ghcarvalho.portfolio.project.infra.persistence.experience.ExperienceJpaRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CompanyExperienceIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private CompanyJpaRepository companyJpaRepository;

    @Autowired
    private ExperienceJpaRepository experienceJpaRepository;

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

        experienceJpaRepository.deleteAll();
        companyJpaRepository.deleteAll();
        adminUserRepository.deleteAll();

        AdminUserEntity admin = adminUserRepository.save(
                new AdminUserEntity("admin", passwordEncoder.encode("admin123456789"))
        );
        String token = jwtUtil.generateToken(admin.getUsername());
        adminAuthCookie = new Cookie("auth_token", token);
    }

    @Test
    @DisplayName("Should create Company in DRAFT, add experiences, publish company, and verify public visibility")
    void shouldManageCompanyPublicationLifecycleAndExperiences() throws Exception {
        // 1. Create a Company via Admin API (defaults to DRAFT)
        CompanyRequestDto companyRequest = new CompanyRequestDto(
                "Acme Corp",
                "acme-corp",
                "Empresa global de tecnologia",
                "https://acme.com",
                "https://acme.com/logo.png"
        );

        MvcResult companyResult = mockMvc.perform(post("/api/v1/admin/companies")
                        .with(csrf())
                        .cookie(adminAuthCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Acme Corp"))
                .andExpect(jsonPath("$.slug").value("acme-corp"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn();

        String companyResponseStr = companyResult.getResponse().getContentAsString();
        UUID companyId = UUID.fromString(objectMapper.readTree(companyResponseStr).get("id").asText());

        // 2. Create Experience 1 for Acme Corp
        ExperienceRequestDto exp1 = new ExperienceRequestDto(
                "junior-developer-acme",
                companyId,
                null,
                "Desenvolvedor Júnior",
                EmploymentType.FULL_TIME,
                "São Paulo, SP",
                "Atuação no backend Java",
                LocalDate.of(2021, 1, 1),
                LocalDate.of(2022, 6, 30),
                false,
                1,
                List.of("Java", "Spring Boot")
        );

        mockMvc.perform(post("/api/v1/admin/experiences")
                        .with(csrf())
                        .cookie(adminAuthCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exp1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("junior-developer-acme"))
                .andExpect(jsonPath("$.company").value("Acme Corp"))
                .andExpect(jsonPath("$.companyId").value(companyId.toString()))
                .andExpect(jsonPath("$.status").doesNotExist()); // No individual status on Experience

        // 3. Since company is DRAFT, it should NOT appear on public endpoints
        mockMvc.perform(get("/api/v1/public/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/api/v1/public/companies/acme-corp"))
                .andExpect(status().isNotFound());

        // 4. Publish company via Admin PATCH endpoint
        mockMvc.perform(patch("/api/v1/admin/companies/" + companyId + "/publish")
                        .with(csrf())
                        .cookie(adminAuthCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        // 5. Create Experience 2 for Acme Corp (promotion!) - automatically available on public frontend
        ExperienceRequestDto exp2 = new ExperienceRequestDto(
                "senior-developer-acme",
                companyId,
                null,
                "Desenvolvedor Sênior",
                EmploymentType.FULL_TIME,
                "Remoto",
                "Liderança técnica e arquitetura",
                LocalDate.of(2022, 7, 1),
                null,
                true,
                2,
                List.of("Java", "PostgreSQL", "Docker")
        );

        mockMvc.perform(post("/api/v1/admin/experiences")
                        .with(csrf())
                        .cookie(adminAuthCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exp2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("senior-developer-acme"))
                .andExpect(jsonPath("$.company").value("Acme Corp"))
                .andExpect(jsonPath("$.status").doesNotExist());

        // 6. Verify published company in public endpoint with both experiences
        mockMvc.perform(get("/api/v1/public/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Acme Corp"))
                .andExpect(jsonPath("$[0].status").value("PUBLISHED"))
                .andExpect(jsonPath("$[0].experiences", hasSize(2)))
                .andExpect(jsonPath("$[0].experiences[0].status").doesNotExist())
                .andExpect(jsonPath("$[0].experiences[1].status").doesNotExist());

        mockMvc.perform(get("/api/v1/public/companies/acme-corp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acme Corp"))
                .andExpect(jsonPath("$.experiences", hasSize(2)));

        // 7. Unpublish company - hides it immediately from public endpoint
        mockMvc.perform(patch("/api/v1/admin/companies/" + companyId + "/unpublish")
                        .with(csrf())
                        .cookie(adminAuthCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));

        mockMvc.perform(get("/api/v1/public/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/api/v1/public/companies/acme-corp"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("A published company without experiences should not appear in public companies list")
    void publishedCompanyWithoutExperiencesShouldNotAppearPublicly() throws Exception {
        CompanyRequestDto companyRequest = new CompanyRequestDto(
                "Empty Corp",
                "empty-corp",
                "Sem experiências ainda",
                null,
                null
        );

        MvcResult result = mockMvc.perform(post("/api/v1/admin/companies")
                        .with(csrf())
                        .cookie(adminAuthCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID companyId = UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());

        // Publish it
        mockMvc.perform(patch("/api/v1/admin/companies/" + companyId + "/publish")
                        .with(csrf())
                        .cookie(adminAuthCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        // Should not appear on public list because it has no experiences
        mockMvc.perform(get("/api/v1/public/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should automatically create Company when Experience is created by company name (compatibility)")
    void shouldAutoCreateCompanyWhenExperienceCreatedByName() throws Exception {
        ExperienceRequestDto request = new ExperienceRequestDto(
                "frontend-engineer-beta",
                null,
                "Beta Solutions",
                "Frontend Engineer",
                EmploymentType.CONTRACT,
                "Florianópolis, SC",
                "Desenvolvimento com React e TypeScript",
                LocalDate.of(2023, 3, 1),
                null,
                true,
                0,
                List.of("React", "TypeScript")
        );

        mockMvc.perform(post("/api/v1/admin/experiences")
                        .with(csrf())
                        .cookie(adminAuthCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.company").value("Beta Solutions"))
                .andExpect(jsonPath("$.companyId").isNotEmpty())
                .andExpect(jsonPath("$.companyDetails.name").value("Beta Solutions"));

        assertThat(companyJpaRepository.findByNameIgnoreCase("Beta Solutions")).isPresent();
        assertThat(companyJpaRepository.findByNameIgnoreCase("Beta Solutions").get().getStatus())
                .isEqualTo(PublicationStatus.DRAFT);
    }

    @Test
    @DisplayName("Deleting Company should cascade and delete its associated Experiences")
    void shouldCascadeDeleteExperiencesWhenCompanyIsDeleted() throws Exception {
        CompanyRequestDto companyRequest = new CompanyRequestDto(
                "Gamma Tech",
                "gamma-tech",
                "Tech",
                null,
                null
        );

        MvcResult companyResult = mockMvc.perform(post("/api/v1/admin/companies")
                        .with(csrf())
                        .cookie(adminAuthCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID companyId = UUID.fromString(objectMapper.readTree(companyResult.getResponse().getContentAsString()).get("id").asText());

        ExperienceRequestDto exp = new ExperienceRequestDto(
                "dev-gamma",
                companyId,
                null,
                "Software Engineer",
                EmploymentType.FULL_TIME,
                null,
                "Atividades gerais",
                LocalDate.of(2024, 1, 1),
                null,
                true,
                1,
                List.of()
        );

        mockMvc.perform(post("/api/v1/admin/experiences")
                        .with(csrf())
                        .cookie(adminAuthCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exp)))
                .andExpect(status().isCreated());

        assertThat(experienceJpaRepository.findAll()).hasSize(1);

        // Delete Company
        mockMvc.perform(delete("/api/v1/admin/companies/" + companyId)
                        .with(csrf())
                        .cookie(adminAuthCookie))
                .andExpect(status().isNoContent());

        assertThat(companyJpaRepository.findById(companyId)).isEmpty();
        assertThat(experienceJpaRepository.findAll()).isEmpty();
    }
}
