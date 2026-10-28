package com.example.ghcarvalho.portfolio.project.infra.persistence.project;

import org.springframework.stereotype.Component;

import com.example.ghcarvalho.portfolio.project.domain.project.Project;

@Component
public class ProjectPersistenceMapper {

    public ProjectEntity toEntity(Project project) {
        return new ProjectEntity(
                project.getId(),
                project.getTitle(),
                project.getSlug(),
                project.getSummary(),
                project.getContent(),
                project.isFeatured(),
                project.getDisplayOrder(),
                project.getStatus(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    public Project toDomain(ProjectEntity entity) {
        return Project.restore(
                entity.getId(),
                entity.getTitle(),
                entity.getSlug(),
                entity.getSummary(),
                entity.getContent(),
                entity.isFeatured(),
                entity.getDisplayOrder(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}