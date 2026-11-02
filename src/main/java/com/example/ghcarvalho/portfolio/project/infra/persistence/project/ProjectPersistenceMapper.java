package com.example.ghcarvalho.portfolio.project.infra.persistence.project;

import org.springframework.stereotype.Component;

import com.example.ghcarvalho.portfolio.project.domain.project.Project;

@Component
public class ProjectPersistenceMapper {

    public ProjectEntity toEntity(Project project) {
        return new ProjectEntity(
                project.getId(),
                project.getNumber(),
                project.getTitle(),
                project.getSlug(),
                project.getCategory(),
                project.getStatus(),
                project.getPublicationStatus(),
                project.getDescription(),
                project.getHighlights(),
                project.getTechnologies(),
                project.getLiveUrl(),
                project.getGithubUrl(),
                project.getImage(),
                project.isFeatured(),
                project.getDisplayOrder(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    public Project toDomain(ProjectEntity entity) {
        return Project.restore(
                entity.getId(),
                entity.getNumber(),
                entity.getTitle(),
                entity.getSlug(),
                entity.getCategory(),
                entity.getStatus(),
                entity.getPublicationStatus(),
                entity.getDescription(),
                entity.getHighlights(),
                entity.getTechnologies(),
                entity.getLiveUrl(),
                entity.getGithubUrl(),
                entity.getImage(),
                entity.isFeatured(),
                entity.getDisplayOrder(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
