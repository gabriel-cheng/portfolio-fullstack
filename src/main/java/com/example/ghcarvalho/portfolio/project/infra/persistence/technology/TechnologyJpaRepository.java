package com.example.ghcarvalho.portfolio.project.infra.persistence.technology;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TechnologyJpaRepository extends JpaRepository<TechnologyEntity, UUID> {

    Optional<TechnologyEntity> findByNameIgnoreCase(String name);

    Optional<TechnologyEntity> findBySlug(String slug);

    boolean existsByNameIgnoreCase(String name);

    List<TechnologyEntity> findAllByOrderByNameAsc();
}

