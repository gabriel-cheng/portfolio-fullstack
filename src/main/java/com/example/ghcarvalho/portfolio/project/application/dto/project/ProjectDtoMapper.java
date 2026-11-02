package com.example.ghcarvalho.portfolio.project.application.dto.project;

import org.springframework.stereotype.Component;

import com.example.ghcarvalho.portfolio.project.application.command.project.CreateProjectCommand;
import com.example.ghcarvalho.portfolio.project.application.command.project.UpdateProjectCommand;

import java.util.UUID;

@Component
public class ProjectDtoMapper {

    public CreateProjectCommand toCommand(CreateProjectDto request) {
        return new CreateProjectCommand(
                request.number(),
                request.title(),
                request.slug(),
                request.category(),
                request.status(),
                request.description(),
                request.highlights(),
                request.technologies(),
                request.liveUrl(),
                request.githubUrl(),
                request.image(),
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
                request.number(),
                request.title(),
                request.slug(),
                request.category(),
                request.status(),
                request.description(),
                request.highlights(),
                request.technologies(),
                request.liveUrl(),
                request.githubUrl(),
                request.image(),
                request.featured(),
                request.displayOrder()
        );
    }
}
