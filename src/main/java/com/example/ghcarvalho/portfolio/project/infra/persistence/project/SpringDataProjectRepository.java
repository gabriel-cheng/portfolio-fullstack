package com.example.ghcarvalho.portfolio.project.infra.persistence.project;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ghcarvalho.portfolio.project.domain.project.ProjectStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProjectRepository
        extends JpaRepository<ProjectEntity, UUID> {

    Optional<ProjectEntity> findBySlug(String slug);

    Optional<ProjectEntity> findBySlugAndStatus(
            String slug,
            ProjectStatus status
    );

    List<ProjectEntity> findAllByOrderByDisplayOrderAsc();

    List<ProjectEntity> findAllByStatusOrderByDisplayOrderAsc(
            ProjectStatus status
    );
    
    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
    
}