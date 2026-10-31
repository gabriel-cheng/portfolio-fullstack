package com.example.ghcarvalho.portfolio.project.domain.company;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {

    Company save(Company company);

    Optional<Company> findById(UUID id);

    Optional<Company> findBySlug(String slug);

    Optional<Company> findByName(String name);

    List<Company> findAll();

    List<Company> findAllPublished();

    Optional<Company> findPublishedBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    boolean existsByName(String name);

    void deleteById(UUID id);
}
