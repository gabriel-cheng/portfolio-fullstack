package com.example.ghcarvalho.portfolio;

import com.example.ghcarvalho.portfolio.project.application.dto.project.CreateProjectDto;
import com.example.ghcarvalho.portfolio.project.application.dto.project.UpdateProjectDto;
import com.example.ghcarvalho.portfolio.project.application.dto.technology.TechnologyRequestDto;
import com.example.ghcarvalho.portfolio.project.infra.persistence.project.SpringDataProjectRepository;
import com.example.ghcarvalho.portfolio.project.infra.persistence.technology.TechnologyJpaRepository;
import com.example.ghcarvalho.portfolio.project.infra.persistence.user.AdminUserEntity;
import com.example.ghcarvalho.portfolio.project.infra.persistence.user.AdminUserRepository;
import com.example.ghcarvalho.portfolio.project.infra.security.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProjectAndTechnologyIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private SpringDataProjectRepository projectRepository;

    @Autowired
    private TechnologyJpaRepository technologyRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private Cookie adminAuthCookie;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        AdminUserEntity admin = adminUserRepository.findByUsername("admin_test_proj")
                .orElseGet(() -> adminUserRepository.save(
                        new AdminUserEntity("admin_test_proj", passwordEncoder.encode("secret123"))
                ));

        String token = jwtUtil.generateToken(admin.getUsername());
        this.adminAuthCookie = new Cookie("auth_token", token);
    }

    @Test
    @DisplayName("Deve gerenciar tecnologias: listar, cadastrar com case-insensitive")
    void shouldManageTechnologies() throws Exception {
        // Criar nova tecnologia
        TechnologyRequestDto request = new TechnologyRequestDto("Kotlin");
        mockMvc.perform(post("/api/v1/admin/technologies")
                        .cookie(adminAuthCookie)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("Kotlin"))
                .andExpect(jsonPath("$.slug").value("kotlin"));

        // Tentativa de criar com case-insensitive diferente deve retornar a existente
        TechnologyRequestDto duplicate = new TechnologyRequestDto("KOTLIN");
        mockMvc.perform(post("/api/v1/admin/technologies")
                        .cookie(adminAuthCookie)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Kotlin"));

        // Listar tecnologias públicas
        mockMvc.perform(get("/api/v1/public/technologies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem("Kotlin")));
    }

    @Test
    @DisplayName("Deve gerenciar projetos e retornar formato solicitado no endpoint público")
    void shouldManageProjectsAndReturnExpectedPublicFormat() throws Exception {
        String testSlug = "vaultapass-" + UUID.randomUUID().toString().substring(0, 8);

        CreateProjectDto createDto = new CreateProjectDto(
                "01",
                "VaultaPass",
                testSlug,
                "Full-Stack & Segurança",
                "Em desenvolvimento",
                "Gerenciador de credenciais e senhas com foco em arquitetura de segurança, criptografia robusta e experiência de uso fluida.",
                List.of(
                        "Criptografia de dados sensíveis e autenticação stateless via tokens JWT",
                        "API RESTful desenvolvida em Java com Spring Boot e validações rígidas",
                        "Interface reativa e intuitiva construída com React, TypeScript e Tailwind CSS"
                ),
                List.of("Java", "Spring Boot", "React", "TypeScript", "PostgreSQL", "Docker"),
                "https://github.com/gabriel-cheng",
                "https://github.com/gabriel-cheng",
                "",
                true,
                1
        );

        // 1. Criar projeto no admin
        MvcResult createResult = mockMvc.perform(post("/api/v1/admin/projects")
                        .cookie(adminAuthCookie)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.number").value("01"))
                .andExpect(jsonPath("$.title").value("VaultaPass"))
                .andExpect(jsonPath("$.slug").value(testSlug))
                .andExpect(jsonPath("$.category").value("Full-Stack & Segurança"))
                .andExpect(jsonPath("$.status").value("Em desenvolvimento"))
                .andExpect(jsonPath("$.publicationStatus").value("DRAFT"))
                .andReturn();

        JsonNode createdJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String projectId = createdJson.get("id").asText();

        // Projeto em DRAFT não pode aparecer no endpoint público
        mockMvc.perform(get("/api/v1/public/projects/" + testSlug))
                .andExpect(status().isNotFound());

        // 2. Publicar projeto
        mockMvc.perform(patch("/api/v1/admin/projects/" + projectId + "/publish")
                        .cookie(adminAuthCookie)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicationStatus").value("PUBLISHED"));

        // 3. Endpoint público deve retornar o formato especificado pelo usuário
        MvcResult publicResult = mockMvc.perform(get("/api/v1/public/projects/" + testSlug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("01"))
                .andExpect(jsonPath("$.title").value("VaultaPass"))
                .andExpect(jsonPath("$.category").value("Full-Stack & Segurança"))
                .andExpect(jsonPath("$.status").value("Em desenvolvimento"))
                .andExpect(jsonPath("$.description").value("Gerenciador de credenciais e senhas com foco em arquitetura de segurança, criptografia robusta e experiência de uso fluida."))
                .andExpect(jsonPath("$.highlights[0]").value("Criptografia de dados sensíveis e autenticação stateless via tokens JWT"))
                .andExpect(jsonPath("$.highlights[1]").value("API RESTful desenvolvida em Java com Spring Boot e validações rígidas"))
                .andExpect(jsonPath("$.highlights[2]").value("Interface reativa e intuitiva construída com React, TypeScript e Tailwind CSS"))
                .andExpect(jsonPath("$.technologies[0]").value("Java"))
                .andExpect(jsonPath("$.technologies[1]").value("Spring Boot"))
                .andExpect(jsonPath("$.technologies[2]").value("React"))
                .andExpect(jsonPath("$.technologies[3]").value("TypeScript"))
                .andExpect(jsonPath("$.technologies[4]").value("PostgreSQL"))
                .andExpect(jsonPath("$.technologies[5]").value("Docker"))
                .andExpect(jsonPath("$.liveUrl").value("https://github.com/gabriel-cheng"))
                .andExpect(jsonPath("$.githubUrl").value("https://github.com/gabriel-cheng"))
                .andExpect(jsonPath("$.image").value(""))
                .andReturn();

        JsonNode publicJson = objectMapper.readTree(publicResult.getResponse().getContentAsString());
        assertThat(publicJson.has("id")).isFalse();
        assertThat(publicJson.has("publicationStatus")).isFalse();
        assertThat(publicJson.has("createdAt")).isFalse();

        // 4. Atualizar projeto no admin
        UpdateProjectDto updateDto = new UpdateProjectDto(
                "02",
                "VaultaPass Updated",
                testSlug,
                "Full-Stack",
                "Concluído",
                "Descrição atualizada.",
                List.of("Destaque único"),
                List.of("Java", "PostgreSQL"),
                "https://live.example.com",
                "https://github.com/gabriel-cheng",
                "/images/proj.png",
                false,
                2
        );

        mockMvc.perform(put("/api/v1/admin/projects/" + projectId)
                        .cookie(adminAuthCookie)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("02"))
                .andExpect(jsonPath("$.title").value("VaultaPass Updated"))
                .andExpect(jsonPath("$.status").value("Concluído"))
                .andExpect(jsonPath("$.technologies.length()").value(2));

        // 5. Despublicar projeto
        mockMvc.perform(patch("/api/v1/admin/projects/" + projectId + "/unpublish")
                        .cookie(adminAuthCookie)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicationStatus").value("DRAFT"));

        mockMvc.perform(get("/api/v1/public/projects/" + testSlug))
                .andExpect(status().isNotFound());

        // 6. Deletar projeto
        mockMvc.perform(delete("/api/v1/admin/projects/" + projectId)
                        .cookie(adminAuthCookie)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/admin/projects/" + projectId)
                        .cookie(adminAuthCookie))
                .andExpect(status().isNotFound());
    }
}

