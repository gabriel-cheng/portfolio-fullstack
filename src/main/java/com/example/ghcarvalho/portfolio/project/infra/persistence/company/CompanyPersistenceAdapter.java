package com.example.ghcarvalho.portfolio.project.infra.persistence.company;

import com.example.ghcarvalho.portfolio.project.domain.company.Company;
import com.example.ghcarvalho.portfolio.project.domain.company.CompanyRepository;
import com.example.ghcarvalho.portfolio.project.domain.experience.Experience;
import com.example.ghcarvalho.portfolio.project.infra.persistence.experience.ExperienceEntity;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CompanyPersistenceAdapter implements CompanyRepository {

    private final CompanyJpaRepository jpaRepository;

    public CompanyPersistenceAdapter(CompanyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Company save(Company company) {
        OffsetDateTime now = OffsetDateTime.now();

        CompanyEntity entity = jpaRepository
                .findById(company.getId())
                .orElseGet(CompanyEntity::new);

        boolean isNew = entity.getId() == null;

        if (isNew) {
            entity.setId(company.getId());
            entity.setCreatedAt(
                    company.getCreatedAt() != null
                            ? company.getCreatedAt()
                            : now
            );
        }

        entity.setName(company.getName());
        entity.setSlug(company.getSlug());
        entity.setDescription(company.getDescription());
        entity.setWebsite(company.getWebsite());
        entity.setLogoUrl(company.getLogoUrl());
        entity.setUpdatedAt(now);

        return toDomain(jpaRepository.save(entity), false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Company> findById(UUID id) {
        return jpaRepository.findById(id).map(entity -> toDomain(entity, true));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Company> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(entity -> toDomain(entity, true));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Company> findByName(String name) {
        return jpaRepository.findByNameIgnoreCase(name).map(entity -> toDomain(entity, true));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> findAll() {
        return jpaRepository.findAllByOrderByNameAsc()
                .stream()
                .map(entity -> toDomain(entity, true))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySlugAndIdNot(String slug, UUID id) {
        return jpaRepository.existsBySlugAndIdNot(slug, id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    public Company toDomain(CompanyEntity entity, boolean includeExperiences) {
        List<Experience> experiences = List.of();

        if (includeExperiences && entity.getExperiences() != null) {
            experiences = entity.getExperiences().stream()
                    .map(this::toExperienceDomain)
                    .toList();
        }

        return new Company(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getDescription(),
                entity.getWebsite(),
                entity.getLogoUrl(),
                experiences,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private Experience toExperienceDomain(ExperienceEntity entity) {
        Company shallowCompany = new Company(
                entity.getCompany().getId(),
                entity.getCompany().getName(),
                entity.getCompany().getSlug(),
                entity.getCompany().getDescription(),
                entity.getCompany().getWebsite(),
                entity.getCompany().getLogoUrl(),
                List.of(),
                entity.getCompany().getCreatedAt(),
                entity.getCompany().getUpdatedAt()
        );

        return new Experience(
                entity.getId(),
                entity.getSlug(),
                entity.getCompanyId(),
                shallowCompany,
                entity.getRole(),
                entity.getEmploymentType(),
                entity.getLocation(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isCurrent(),
                entity.getDisplayOrder(),
                entity.getStatus(),
                entity.getTechnologies(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

