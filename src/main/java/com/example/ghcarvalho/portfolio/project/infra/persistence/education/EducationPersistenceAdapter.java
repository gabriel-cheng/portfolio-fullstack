package com.example.ghcarvalho.portfolio.project.infra.persistence.education;

import com.example.ghcarvalho.portfolio.project.domain.education.Education;
import com.example.ghcarvalho.portfolio.project.domain.education.EducationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EducationPersistenceAdapter implements EducationRepository {

    private final EducationJpaRepository jpaRepository;

    public EducationPersistenceAdapter(EducationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Education save(Education education) {
        OffsetDateTime now = OffsetDateTime.now();

        EducationEntity entity = jpaRepository
                .findById(education.getId())
                .orElseGet(EducationEntity::new);

        boolean isNew = entity.getId() == null;

        if (isNew) {
            entity.setId(education.getId());
            entity.setCreatedAt(
                    education.getCreatedAt() != null
                            ? education.getCreatedAt()
                            : now
            );
        }

        entity.setDegree(education.getDegree());
        entity.setInstitution(education.getInstitution());
        entity.setStartYear(education.getStartYear());
        entity.setEndYear(education.getEndYear());
        entity.setStatus(education.getStatus());
        entity.setType(education.getType());
        entity.setDescription(education.getDescription());
        entity.setSkills(education.getSkills());
        entity.setDiplomaImage(education.getDiplomaImage());
        entity.setDiplomaAlt(education.getDiplomaAlt());
        entity.setDisplayOrder(education.getDisplayOrder());
        entity.setUpdatedAt(now);

        return toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Education> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Education> findAll() {
        return jpaRepository
                .findAllByOrderByDisplayOrderAscStartYearDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private Education toDomain(EducationEntity entity) {
        return new Education(
                entity.getId(),
                entity.getDegree(),
                entity.getInstitution(),
                entity.getStartYear(),
                entity.getEndYear(),
                entity.getStatus(),
                entity.getType(),
                entity.getDescription(),
                entity.getSkills(),
                entity.getDiplomaImage(),
                entity.getDiplomaAlt(),
                entity.getDisplayOrder(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

