package com.example.ghcarvalho.portfolio.project.application.dto.project;

import com.example.ghcarvalho.portfolio.project.domain.project.Project;

public final class ProjectResponseMapper {

    private ProjectResponseMapper() {
    }

    public static ProjectResponseDto toResponse(Project project) {
        return new ProjectResponseDto(
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
}