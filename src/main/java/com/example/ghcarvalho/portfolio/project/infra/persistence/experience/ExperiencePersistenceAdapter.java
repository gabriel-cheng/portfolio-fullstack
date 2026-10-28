package com.example.ghcarvalho.portfolio.project.infra.persistence.experience;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.ghcarvalho.portfolio.project.domain.experience.Experience;
import com.example.ghcarvalho.portfolio.project.domain.experience.ExperienceRepository;
import com.example.ghcarvalho.portfolio.project.domain.experience.ExperienceStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ExperiencePersistenceAdapter implements ExperienceRepository {

    private final ExperienceJpaRepository jpaRepository;

    public ExperiencePersistenceAdapter(
            ExperienceJpaRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
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

        entity.setSlug(experience.getSlug());
        entity.setCompany(experience.getCompany());
        entity.setRole(experience.getRole());
        entity.setEmploymentType(experience.getEmploymentType());
        entity.setLocation(experience.getLocation());
        entity.setDescription(experience.getDescription());
        entity.setStartDate(experience.getStartDate());
        entity.setEndDate(experience.getEndDate());
        entity.setCurrent(experience.isCurrent());
        entity.setDisplayOrder(experience.getDisplayOrder());
        entity.setStatus(experience.getStatus());
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
    public List<Experience> findAllPublished() {
        return jpaRepository
                .findAllByStatusOrderByDisplayOrderAscStartDateDesc(
                        ExperienceStatus.PUBLISHED
                )
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
        return new Experience(
                entity.getId(),
                entity.getSlug(),
                entity.getCompany(),
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