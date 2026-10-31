package com.example.ghcarvalho.portfolio.project.domain.experience;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExperienceRepository {

    Experience save(Experience experience);

    Optional<Experience> findById(UUID id);

    Optional<Experience> findBySlug(String slug);

    List<Experience> findAll();

    List<Experience> findAllByCompanyId(UUID companyId);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    void deleteById(UUID id);
}
