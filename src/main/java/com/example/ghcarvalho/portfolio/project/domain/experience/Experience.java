package com.example.ghcarvalho.portfolio.project.domain.experience;

import com.example.ghcarvalho.portfolio.project.domain.company.Company;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Experience {

    private final UUID id;
    private final String slug;
    private final UUID companyId;
    private final Company company;
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
            UUID companyId,
            Company company,
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
        this.id = Objects.requireNonNull(id, "ID não pode ser nulo.");
        this.slug = requireText(slug, "Slug");
        this.companyId = Objects.requireNonNull(companyId, "ID da empresa não pode ser nulo.");
        this.company = company;
        this.role = requireText(role, "Cargo");
        this.employmentType = Objects.requireNonNull(employmentType, "Tipo de vínculo não pode ser nulo.");
        this.location = location;
        this.description = requireText(description, "Descrição");
        this.startDate = Objects.requireNonNull(startDate, "Data de início não pode ser nula.");
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
        this.status = Objects.requireNonNull(status, "Status não pode ser nulo.");
        this.technologies = technologies == null
                ? List.of()
                : List.copyOf(technologies);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Experience(
            UUID id,
            String slug,
            UUID companyId,
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
        this(
                id,
                slug,
                companyId,
                null,
                role,
                employmentType,
                location,
                description,
                startDate,
                endDate,
                current,
                displayOrder,
                status,
                technologies,
                createdAt,
                updatedAt
        );
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " não pode ser vazio."
            );
        }

        return value.trim();
    }

    public Experience withCompany(Company company) {
        return new Experience(
                this.id,
                this.slug,
                company != null ? company.getId() : this.companyId,
                company,
                this.role,
                this.employmentType,
                this.location,
                this.description,
                this.startDate,
                this.endDate,
                this.current,
                this.displayOrder,
                this.status,
                this.technologies,
                this.createdAt,
                this.updatedAt
        );
    }

    public UUID getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public Company getCompany() {
        return company;
    }

    public String getCompanyName() {
        return company != null ? company.getName() : null;
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
