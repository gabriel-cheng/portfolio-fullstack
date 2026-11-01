package com.example.ghcarvalho.portfolio.project.domain.education;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Education {

    private final UUID id;
    private final String degree;
    private final String institution;
    private final String startYear;
    private final String endYear;
    private final String status;
    private final String type;
    private final String description;
    private final List<String> skills;
    private final String diplomaImage;
    private final String diplomaAlt;
    private final int displayOrder;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public Education(
            UUID id,
            String degree,
            String institution,
            String startYear,
            String endYear,
            String status,
            String type,
            String description,
            List<String> skills,
            String diplomaImage,
            String diplomaAlt,
            int displayOrder,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "ID não pode ser nulo.");
        this.degree = requireText(degree, "Curso/Grau (degree)");
        this.institution = requireText(institution, "Instituição (institution)");
        this.startYear = requireText(startYear, "Ano de início (startYear)");
        this.endYear = endYear != null && !endYear.isBlank() ? endYear.trim() : null;
        this.status = requireText(status, "Status da formação");
        this.type = requireText(type, "Tipo da formação (type)");
        this.description = description;
        this.skills = skills == null ? List.of() : List.copyOf(skills);
        this.diplomaImage = diplomaImage;
        this.diplomaAlt = diplomaAlt;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " não pode ser vazio.");
        }
        return value.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getDegree() {
        return degree;
    }

    public String getInstitution() {
        return institution;
    }

    public String getStartYear() {
        return startYear;
    }

    public String getEndYear() {
        return endYear;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getSkills() {
        return skills;
    }

    public String getDiplomaImage() {
        return diplomaImage;
    }

    public String getDiplomaAlt() {
        return diplomaAlt;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}

