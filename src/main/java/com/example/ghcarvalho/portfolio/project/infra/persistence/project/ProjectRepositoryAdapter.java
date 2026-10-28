package com.example.ghcarvalho.portfolio.project.infra.persistence.project;

import org.springframework.stereotype.Repository;

import com.example.ghcarvalho.portfolio.project.domain.project.Project;
import com.example.ghcarvalho.portfolio.project.domain.project.ProjectRepository;
import com.example.ghcarvalho.portfolio.project.domain.project.ProjectStatus;

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
    public Project save(Project project) {
        ProjectEntity entity = mapper.toEntity(project);
        ProjectEntity savedEntity = springDataRepository.save(entity);

        return mapper.toDomain(savedEntity);
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
                .findBySlugAndStatus(slug, ProjectStatus.PUBLISHED)
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
                .findAllByStatusOrderByDisplayOrderAsc(
                        ProjectStatus.PUBLISHED
                )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
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