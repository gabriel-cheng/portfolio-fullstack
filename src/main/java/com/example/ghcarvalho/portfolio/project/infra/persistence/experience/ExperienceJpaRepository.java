package com.example.ghcarvalho.portfolio.project.infra.persistence.experience;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExperienceJpaRepository
        extends JpaRepository<ExperienceEntity, UUID> {

    Optional<ExperienceEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    List<ExperienceEntity> findAllByOrderByDisplayOrderAscStartDateDesc();

    List<ExperienceEntity> findAllByCompanyIdOrderByDisplayOrderAscStartDateDesc(
            UUID companyId
    );
}
