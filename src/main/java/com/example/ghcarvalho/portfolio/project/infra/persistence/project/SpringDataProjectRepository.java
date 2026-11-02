package com.example.ghcarvalho.portfolio.project.infra.persistence.project;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ghcarvalho.portfolio.project.domain.company.PublicationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProjectRepository
        extends JpaRepository<ProjectEntity, UUID> {

    Optional<ProjectEntity> findBySlug(String slug);

    Optional<ProjectEntity> findBySlugAndPublicationStatus(
            String slug,
            PublicationStatus publicationStatus
    );

    List<ProjectEntity> findAllByOrderByDisplayOrderAsc();

    List<ProjectEntity> findAllByPublicationStatusOrderByDisplayOrderAsc(
            PublicationStatus publicationStatus
    );

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}
