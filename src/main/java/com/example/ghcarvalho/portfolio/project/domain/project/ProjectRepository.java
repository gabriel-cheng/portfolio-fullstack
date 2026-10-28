package com.example.ghcarvalho.portfolio.project.domain.project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(UUID id);

    Optional<Project> findBySlug(String slug);

    Optional<Project> findPublishedBySlug(String slug);

    List<Project> findAll();

    List<Project> findAllPublished();

    void deleteById(UUID id);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}