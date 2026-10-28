package com.example.ghcarvalho.portfolio.project.application.dto.project;

import com.example.ghcarvalho.portfolio.project.application.command.project.CreateProjectCommand;
import com.example.ghcarvalho.portfolio.project.application.command.project.UpdateProjectCommand;
import com.example.ghcarvalho.portfolio.project.domain.project.Project;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProjectDtoMapper {

    public CreateProjectCommand toCommand(CreateProjectDto request) {
        return new CreateProjectCommand(
                request.title(),
                request.slug(),
                request.summary(),
                request.content(),
                request.featured(),
                request.displayOrder()
        );
    }

    public UpdateProjectCommand toCommand(
            UUID id,
            UpdateProjectDto request
    ) {
        return new UpdateProjectCommand(
                id,
                request.title(),
                request.slug(),
                request.summary(),
                request.content(),
                request.featured(),
                request.displayOrder()
        );
    }

    public ProjectDto toResponse(Project project) {
        return new ProjectDto(
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