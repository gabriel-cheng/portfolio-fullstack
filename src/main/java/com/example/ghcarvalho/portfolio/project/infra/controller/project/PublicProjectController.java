package com.example.ghcarvalho.portfolio.project.infra.controller.project;

import com.example.ghcarvalho.portfolio.project.application.dto.project.ProjectResponseDto;
import com.example.ghcarvalho.portfolio.project.application.dto.project.ProjectResponseMapper;
import com.example.ghcarvalho.portfolio.project.application.query.ProjectQueryService;
import com.example.ghcarvalho.portfolio.project.domain.project.Project;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/projects")
public class PublicProjectController {

    private final ProjectQueryService projectQueryService;

    public PublicProjectController(
            ProjectQueryService projectQueryService
    ) {
        this.projectQueryService = projectQueryService;
    }

    @GetMapping
    public List<ProjectResponseDto> findAllPublished() {
        return projectQueryService.findAllPublished()
                .stream()
                .map(ProjectResponseMapper::toResponse)
                .toList();
    }

    @GetMapping("/{slug}")
    public ProjectResponseDto findPublishedBySlug(
            @PathVariable String slug
    ) {
        Project project = projectQueryService.findPublishedBySlug(slug);

        return ProjectResponseMapper.toResponse(project);
    }
}