package com.example.ghcarvalho.portfolio.project.infra.persistence.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, UUID> {

    Optional<CompanyEntity> findBySlug(String slug);

    Optional<CompanyEntity> findByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    boolean existsByNameIgnoreCase(String name);

    List<CompanyEntity> findAllByOrderByNameAsc();
}

