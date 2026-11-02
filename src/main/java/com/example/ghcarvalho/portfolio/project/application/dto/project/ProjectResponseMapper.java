package com.example.ghcarvalho.portfolio.project.application.dto.project;

import com.example.ghcarvalho.portfolio.project.domain.project.Project;

public final class ProjectResponseMapper {

    private ProjectResponseMapper() {
    }

    public static ProjectResponseDto toResponse(Project project) {
        return new ProjectResponseDto(
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

    public static PublicProjectResponseDto toPublicResponse(Project project) {
        return new PublicProjectResponseDto(
                project.getNumber(),
                project.getTitle(),
                project.getCategory(),
                project.getStatus(),
                project.getDescription(),
                project.getHighlights(),
                project.getTechnologies(),
                project.getLiveUrl(),
                project.getGithubUrl(),
                project.getImage()
        );
    }
}
