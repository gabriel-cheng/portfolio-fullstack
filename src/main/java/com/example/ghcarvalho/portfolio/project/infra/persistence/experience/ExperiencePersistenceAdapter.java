package com.example.ghcarvalho.portfolio.project.infra.persistence.experience;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.ghcarvalho.portfolio.project.domain.company.Company;
import com.example.ghcarvalho.portfolio.project.domain.experience.Experience;
import com.example.ghcarvalho.portfolio.project.domain.experience.ExperienceRepository;
import com.example.ghcarvalho.portfolio.project.infra.persistence.company.CompanyEntity;
import com.example.ghcarvalho.portfolio.project.infra.persistence.company.CompanyJpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ExperiencePersistenceAdapter implements ExperienceRepository {

    private final ExperienceJpaRepository jpaRepository;
    private final CompanyJpaRepository companyJpaRepository;

    public ExperiencePersistenceAdapter(
            ExperienceJpaRepository jpaRepository,
            CompanyJpaRepository companyJpaRepository
    ) {
        this.jpaRepository = jpaRepository;
        this.companyJpaRepository = companyJpaRepository;
    }

    @Override
    @Transactional
    public Experience save(Experience experience) {
        OffsetDateTime now = OffsetDateTime.now();

        ExperienceEntity entity = jpaRepository
                .findById(experience.getId())
                .orElseGet(ExperienceEntity::new);

        boolean isNew = entity.getId() == null;

        if (isNew) {
            entity.setId(experience.getId());
            entity.setCreatedAt(
                    experience.getCreatedAt() != null
                            ? experience.getCreatedAt()
                            : now
            );
        }

        CompanyEntity companyEntity = companyJpaRepository
                .findById(experience.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Empresa não encontrada para o ID fornecido: " + experience.getCompanyId()
                ));

        entity.setSlug(experience.getSlug());
        entity.setCompany(companyEntity);
        entity.setRole(experience.getRole());
        entity.setEmploymentType(experience.getEmploymentType());
        entity.setLocation(experience.getLocation());
        entity.setDescription(experience.getDescription());
        entity.setStartDate(experience.getStartDate());
        entity.setEndDate(experience.getEndDate());
        entity.setCurrent(experience.isCurrent());
        entity.setDisplayOrder(experience.getDisplayOrder());
        entity.setTechnologies(experience.getTechnologies());
        entity.setUpdatedAt(now);

        return toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Experience> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Experience> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Experience> findAll() {
        return jpaRepository
                .findAllByOrderByDisplayOrderAscStartDateDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Experience> findAllByCompanyId(UUID companyId) {
        return jpaRepository
                .findAllByCompanyIdOrderByDisplayOrderAscStartDateDesc(companyId)
                .stream()
                .map(this::toDomain)
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
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private Experience toDomain(ExperienceEntity entity) {
        Company company = null;
        if (entity.getCompany() != null) {
            company = new Company(
                    entity.getCompany().getId(),
                    entity.getCompany().getName(),
                    entity.getCompany().getSlug(),
                    entity.getCompany().getDescription(),
                    entity.getCompany().getWebsite(),
                    entity.getCompany().getLogoUrl(),
                    entity.getCompany().getStatus(),
                    List.of(),
                    entity.getCompany().getCreatedAt(),
                    entity.getCompany().getUpdatedAt()
            );
        }

        return new Experience(
                entity.getId(),
                entity.getSlug(),
                entity.getCompanyId(),
                company,
                entity.getRole(),
                entity.getEmploymentType(),
                entity.getLocation(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isCurrent(),
                entity.getDisplayOrder(),
                entity.getTechnologies(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
