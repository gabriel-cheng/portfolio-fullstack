package com.example.ghcarvalho.portfolio.project.domain.company;

import com.example.ghcarvalho.portfolio.project.domain.experience.Experience;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Company {

    private final UUID id;
    private final String name;
    private final String slug;
    private final String description;
    private final String website;
    private final String logoUrl;
    private final PublicationStatus status;
    private final List<Experience> experiences;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public Company(
            UUID id,
            String name,
            String slug,
            String description,
            String website,
            String logoUrl,
            PublicationStatus status,
            List<Experience> experiences,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "ID não pode ser nulo.");
        this.name = requireText(name, "Nome da empresa");
        this.slug = requireText(slug, "Slug da empresa");
        this.description = description;
        this.website = website;
        this.logoUrl = logoUrl;
        this.status = status != null ? status : PublicationStatus.DRAFT;
        this.experiences = experiences == null ? List.of() : List.copyOf(experiences);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Company(
            UUID id,
            String name,
            String slug,
            String description,
            String website,
            String logoUrl,
            List<Experience> experiences,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this(id, name, slug, description, website, logoUrl, PublicationStatus.DRAFT, experiences, createdAt, updatedAt);
    }

    public Company(
            UUID id,
            String name,
            String slug,
            String description,
            String website,
            String logoUrl,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this(id, name, slug, description, website, logoUrl, PublicationStatus.DRAFT, List.of(), createdAt, updatedAt);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " não pode ser vazio.");
        }
        return value.trim();
    }

    public Company publish() {
        return withStatus(PublicationStatus.PUBLISHED);
    }

    public Company unpublish() {
        return withStatus(PublicationStatus.DRAFT);
    }

    public Company withStatus(PublicationStatus status) {
        return new Company(
                this.id,
                this.name,
                this.slug,
                this.description,
                this.website,
                this.logoUrl,
                status,
                this.experiences,
                this.createdAt,
                OffsetDateTime.now()
        );
    }

    public Company withExperiences(List<Experience> experiences) {
        return new Company(
                this.id,
                this.name,
                this.slug,
                this.description,
                this.website,
                this.logoUrl,
                this.status,
                experiences,
                this.createdAt,
                this.updatedAt
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public PublicationStatus getStatus() {
        return status;
    }

    public boolean isPublished() {
        return status == PublicationStatus.PUBLISHED;
    }

    public List<Experience> getExperiences() {
        return experiences;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
