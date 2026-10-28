package com.example.ghcarvalho.portfolio.project.application.dto.project;

import org.springframework.stereotype.Component;

import com.example.ghcarvalho.portfolio.project.application.command.project.CreateProjectCommand;
import com.example.ghcarvalho.portfolio.project.application.command.project.UpdateProjectCommand;

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
            java.util.UUID id,
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
}