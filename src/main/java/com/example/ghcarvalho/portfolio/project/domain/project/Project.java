package com.example.ghcarvalho.portfolio.project.domain.project;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Project {

    private final UUID id;
    private String title;
    private String slug;
    private String summary;
    private Map<String, Object> content;
    private boolean featured;
    private int displayOrder;
    private ProjectStatus status;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private Project(
            UUID id,
            String title,
            String slug,
            String summary,
            Map<String, Object> content,
            boolean featured,
            int displayOrder,
            ProjectStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);

        validate(title, slug, summary, status);

        this.title = title.trim();
        this.slug = slug.trim();
        this.summary = summary.trim();
        this.content = normalizeContent(content);
        this.featured = featured;
        this.displayOrder = displayOrder;
        this.status = status;
    }

    public static Project create(
            String title,
            String slug,
            String summary,
            Map<String, Object> content,
            boolean featured,
            int displayOrder
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        return new Project(
                UUID.randomUUID(),
                title,
                slug,
                summary,
                content,
                featured,
                displayOrder,
                ProjectStatus.DRAFT,
                now,
                now
        );
    }

    public static Project restore(
            UUID id,
            String title,
            String slug,
            String summary,
            Map<String, Object> content,
            boolean featured,
            int displayOrder,
            ProjectStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new Project(
                id,
                title,
                slug,
                summary,
                content,
                featured,
                displayOrder,
                status,
                createdAt,
                updatedAt
        );
    }

    public void update(
            String title,
            String slug,
            String summary,
            Map<String, Object> content,
            boolean featured,
            int displayOrder
    ) {
        validate(title, slug, summary, status);

        this.title = title.trim();
        this.slug = slug.trim();
        this.summary = summary.trim();
        this.content = normalizeContent(content);
        this.featured = featured;
        this.displayOrder = displayOrder;
        this.updatedAt = OffsetDateTime.now();
    }

    public void publish() {
        this.status = ProjectStatus.PUBLISHED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void unpublish() {
        this.status = ProjectStatus.DRAFT;
        this.updatedAt = OffsetDateTime.now();
    }

    private static void validate(
            String title,
            String slug,
            String summary,
            ProjectStatus status
    ) {
        requireText(title, "Título");
        requireText(slug, "Slug");
        requireText(summary, "Resumo");
        Objects.requireNonNull(status, "O status é obrigatório.");

        if (title.trim().length() > 200) {
            throw new IllegalArgumentException(
                    "O título não pode ultrapassar 200 caracteres."
            );
        }

        if (slug.trim().length() > 200) {
            throw new IllegalArgumentException(
                    "O slug não pode ultrapassar 200 caracteres."
            );
        }

        if (summary.trim().length() > 500) {
            throw new IllegalArgumentException(
                    "O resumo não pode ultrapassar 500 caracteres."
            );
        }
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " é obrigatório."
            );
        }
    }

    private static Map<String, Object> normalizeContent(
            Map<String, Object> content
    ) {
        if (content == null) {
            return Map.of();
        }

        return Collections.unmodifiableMap(
                new LinkedHashMap<>(content)
        );
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getSummary() {
        return summary;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public boolean isFeatured() {
        return featured;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}