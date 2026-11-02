package com.example.ghcarvalho.portfolio.project.domain.technology;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TechnologyRepository {

    Technology save(Technology technology);

    Optional<Technology> findById(UUID id);

    Optional<Technology> findByNameIgnoreCase(String name);

    Optional<Technology> findBySlug(String slug);

    List<Technology> findAll();

    void deleteById(UUID id);

    boolean existsByNameIgnoreCase(String name);
}

