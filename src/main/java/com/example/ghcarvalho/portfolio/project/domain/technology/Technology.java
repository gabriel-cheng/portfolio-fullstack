package com.example.ghcarvalho.portfolio.project.domain.technology;

import java.text.Normalizer;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Technology {

    private final UUID id;
    private String name;
    private String slug;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private Technology(
            UUID id,
            String name,
            String slug,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "O id é obrigatório.");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt é obrigatório.");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt é obrigatório.");

        validateName(name);

        this.name = name.trim();
        this.slug = (slug != null && !slug.isBlank()) ? slug.trim() : slugify(name);
    }

    public static Technology create(String name) {
        OffsetDateTime now = OffsetDateTime.now();
        return new Technology(
                UUID.randomUUID(),
                name,
                slugify(name),
                now,
                now
        );
    }

    public static Technology restore(
            UUID id,
            String name,
            String slug,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new Technology(
                id,
                name,
                slug,
                createdAt,
                updatedAt
        );
    }

    public void update(String name) {
        validateName(name);
        this.name = name.trim();
        this.slug = slugify(name);
        this.updatedAt = OffsetDateTime.now();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O nome da tecnologia é obrigatório.");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("O nome da tecnologia não pode ultrapassar 100 caracteres.");
        }
    }

    public static String slugify(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String normalized = Normalizer.normalize(input.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String slug = normalized.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        return slug.isBlank() ? "tech-" + UUID.randomUUID().toString().substring(0, 8) : slug;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}

