package com.example.ghcarvalho.portfolio.project.domain.experience;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Experience {

    private final UUID id;
    private final String slug;
    private final String company;
    private final String role;
    private final EmploymentType employmentType;
    private final String location;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final boolean current;
    private final int displayOrder;
    private final ExperienceStatus status;
    private final List<String> technologies;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public Experience(
            UUID id,
            String slug,
            String company,
            String role,
            EmploymentType employmentType,
            String location,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            boolean current,
            int displayOrder,
            ExperienceStatus status,
            List<String> technologies,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.slug = requireText(slug, "Slug");
        this.company = requireText(company, "Empresa");
        this.role = requireText(role, "Cargo");
        this.employmentType = Objects.requireNonNull(employmentType);
        this.location = location;
        this.description = requireText(description, "Descrição");
        this.startDate = Objects.requireNonNull(startDate);
        this.endDate = endDate;
        this.current = current;

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "A data de término não pode ser anterior à data de início."
            );
        }

        if (current && endDate != null) {
            throw new IllegalArgumentException(
                    "Uma experiência atual não pode ter data de término."
            );
        }

        if (displayOrder < 0) {
            throw new IllegalArgumentException(
                    "A ordem de exibição não pode ser negativa."
            );
        }

        this.displayOrder = displayOrder;
        this.status = Objects.requireNonNull(status);
        this.technologies = technologies == null
                ? List.of()
                : List.copyOf(technologies);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " não pode ser vazio."
            );
        }

        return value.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isCurrent() {
        return current;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public ExperienceStatus getStatus() {
        return status;
    }

    public List<String> getTechnologies() {
        return technologies;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}