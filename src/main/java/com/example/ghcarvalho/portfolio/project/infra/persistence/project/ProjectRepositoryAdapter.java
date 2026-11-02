package com.example.ghcarvalho.portfolio.project.infra.persistence.project;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.ghcarvalho.portfolio.project.domain.company.PublicationStatus;
import com.example.ghcarvalho.portfolio.project.domain.project.Project;
import com.example.ghcarvalho.portfolio.project.domain.project.ProjectRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProjectRepositoryAdapter implements ProjectRepository {

    private final SpringDataProjectRepository springDataRepository;
    private final ProjectPersistenceMapper mapper;

    public ProjectRepositoryAdapter(
            SpringDataProjectRepository springDataRepository,
            ProjectPersistenceMapper mapper
    ) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Project save(Project project) {
        OffsetDateTime now = OffsetDateTime.now();

        ProjectEntity entity = springDataRepository
                .findById(project.getId())
                .orElseGet(ProjectEntity::new);

        boolean isNew = entity.getId() == null;

        if (isNew) {
            entity.setId(project.getId());
            entity.setCreatedAt(
                    project.getCreatedAt() != null
                            ? project.getCreatedAt()
                            : now
            );
        }

        entity.setNumber(project.getNumber());
        entity.setTitle(project.getTitle());
        entity.setSlug(project.getSlug());
        entity.setCategory(project.getCategory());
        entity.setStatus(project.getStatus());
        entity.setPublicationStatus(project.getPublicationStatus());
        entity.setDescription(project.getDescription());
        entity.setHighlights(project.getHighlights());
        entity.setTechnologies(project.getTechnologies());
        entity.setLiveUrl(project.getLiveUrl());
        entity.setGithubUrl(project.getGithubUrl());
        entity.setImage(project.getImage());
        entity.setFeatured(project.isFeatured());
        entity.setDisplayOrder(project.getDisplayOrder());
        entity.setUpdatedAt(
                project.getUpdatedAt() != null
                        ? project.getUpdatedAt()
                        : now
        );

        ProjectEntity saved = springDataRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return springDataRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Project> findBySlug(String slug) {
        return springDataRepository.findBySlug(slug)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Project> findPublishedBySlug(String slug) {
        return springDataRepository
                .findBySlugAndPublicationStatus(slug, PublicationStatus.PUBLISHED)
                .map(mapper::toDomain);
    }

    @Override
    public List<Project> findAll() {
        return springDataRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Project> findAllPublished() {
        return springDataRepository
                .findAllByPublicationStatusOrderByDisplayOrderAsc(
                        PublicationStatus.PUBLISHED
                )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return springDataRepository.existsBySlug(slug);
    }

    @Override
    public boolean existsBySlugAndIdNot(String slug, UUID id) {
        return springDataRepository.existsBySlugAndIdNot(slug, id);
    }
}
