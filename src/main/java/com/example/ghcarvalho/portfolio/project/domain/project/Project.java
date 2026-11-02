package com.example.ghcarvalho.portfolio.project.domain.project;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.example.ghcarvalho.portfolio.project.domain.company.PublicationStatus;

public class Project {

    private final UUID id;
    private String number;
    private String title;
    private String slug;
    private String category;
    private String status;
    private PublicationStatus publicationStatus;
    private String description;
    private List<String> highlights;
    private List<String> technologies;
    private String liveUrl;
    private String githubUrl;
    private String image;
    private boolean featured;
    private int displayOrder;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private Project(
            UUID id,
            String number,
            String title,
            String slug,
            String category,
            String status,
            PublicationStatus publicationStatus,
            String description,
            List<String> highlights,
            List<String> technologies,
            String liveUrl,
            String githubUrl,
            String image,
            boolean featured,
            int displayOrder,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "O id é obrigatório.");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt é obrigatório.");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt é obrigatório.");

        validate(number, title, slug, category, status, description, publicationStatus);

        this.number = number != null ? number.trim() : "01";
        this.title = title.trim();
        this.slug = slug.trim();
        this.category = category.trim();
        this.status = status.trim();
        this.publicationStatus = publicationStatus;
        this.description = description.trim();
        this.highlights = normalizeList(highlights);
        this.technologies = normalizeList(technologies);
        this.liveUrl = liveUrl != null ? liveUrl.trim() : "";
        this.githubUrl = githubUrl != null ? githubUrl.trim() : "";
        this.image = image != null ? image.trim() : "";
        this.featured = featured;
        this.displayOrder = displayOrder;
    }

    public static Project create(
            String number,
            String title,
            String slug,
            String category,
            String status,
            String description,
            List<String> highlights,
            List<String> technologies,
            String liveUrl,
            String githubUrl,
            String image,
            boolean featured,
            int displayOrder
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        return new Project(
                UUID.randomUUID(),
                number,
                title,
                slug,
                category,
                status,
                PublicationStatus.DRAFT,
                description,
                highlights,
                technologies,
                liveUrl,
                githubUrl,
                image,
                featured,
                displayOrder,
                now,
                now
        );
    }

    public static Project restore(
            UUID id,
            String number,
            String title,
            String slug,
            String category,
            String status,
            PublicationStatus publicationStatus,
            String description,
            List<String> highlights,
            List<String> technologies,
            String liveUrl,
            String githubUrl,
            String image,
            boolean featured,
            int displayOrder,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new Project(
                id,
                number,
                title,
                slug,
                category,
                status,
                publicationStatus,
                description,
                highlights,
                technologies,
                liveUrl,
                githubUrl,
                image,
                featured,
                displayOrder,
                createdAt,
                updatedAt
        );
    }

    public void update(
            String number,
            String title,
            String slug,
            String category,
            String status,
            String description,
            List<String> highlights,
            List<String> technologies,
            String liveUrl,
            String githubUrl,
            String image,
            boolean featured,
            int displayOrder
    ) {
        validate(number, title, slug, category, status, description, this.publicationStatus);

        this.number = number != null ? number.trim() : "01";
        this.title = title.trim();
        this.slug = slug.trim();
        this.category = category.trim();
        this.status = status.trim();
        this.description = description.trim();
        this.highlights = normalizeList(highlights);
        this.technologies = normalizeList(technologies);
        this.liveUrl = liveUrl != null ? liveUrl.trim() : "";
        this.githubUrl = githubUrl != null ? githubUrl.trim() : "";
        this.image = image != null ? image.trim() : "";
        this.featured = featured;
        this.displayOrder = displayOrder;
        this.updatedAt = OffsetDateTime.now();
    }

    public void publish() {
        this.publicationStatus = PublicationStatus.PUBLISHED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void unpublish() {
        this.publicationStatus = PublicationStatus.DRAFT;
        this.updatedAt = OffsetDateTime.now();
    }

    private static void validate(
            String number,
            String title,
            String slug,
            String category,
            String status,
            String description,
            PublicationStatus publicationStatus
    ) {
        requireText(title, "Título");
        requireText(slug, "Slug");
        requireText(category, "Categoria");
        requireText(status, "Status");
        requireText(description, "Descrição");
        Objects.requireNonNull(publicationStatus, "O status de publicação é obrigatório.");

        if (number != null && number.trim().length() > 10) {
            throw new IllegalArgumentException("O número não pode ultrapassar 10 caracteres.");
        }

        if (title.trim().length() > 200) {
            throw new IllegalArgumentException("O título não pode ultrapassar 200 caracteres.");
        }

        if (slug.trim().length() > 200) {
            throw new IllegalArgumentException("O slug não pode ultrapassar 200 caracteres.");
        }

        if (category.trim().length() > 100) {
            throw new IllegalArgumentException("A categoria não pode ultrapassar 100 caracteres.");
        }

        if (status.trim().length() > 100) {
            throw new IllegalArgumentException("O status não pode ultrapassar 100 caracteres.");
        }
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " é obrigatório.");
        }
    }

    private static List<String> normalizeList(List<String> list) {
        if (list == null) {
            return List.of();
        }
        return Collections.unmodifiableList(
                list.stream()
                        .filter(item -> item != null && !item.isBlank())
                        .map(String::trim)
                        .toList()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public PublicationStatus getPublicationStatus() {
        return publicationStatus;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public List<String> getTechnologies() {
        return technologies;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getImage() {
        return image;
    }

    public boolean isFeatured() {
        return featured;
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
