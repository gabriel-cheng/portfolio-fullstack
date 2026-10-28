package com.example.ghcarvalho.portfolio.project.infra.controller;

import com.example.ghcarvalho.portfolio.project.application.dto.project.ProjectDto;
import com.example.ghcarvalho.portfolio.project.application.dto.project.ProjectDtoMapper;
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
    private final ProjectDtoMapper projectDtoMapper;

    public PublicProjectController(
            ProjectQueryService projectQueryService,
            ProjectDtoMapper projectDtoMapper
    ) {
        this.projectQueryService = projectQueryService;
        this.projectDtoMapper = projectDtoMapper;
    }

    @GetMapping
    public List<ProjectDto> findAllPublished() {
        return projectQueryService.findAllPublished()
                .stream()
                .map(projectDtoMapper::toResponse)
                .toList();
    }

    @GetMapping("/{slug}")
    public ProjectDto findPublishedBySlug(
            @PathVariable String slug
    ) {
        Project project = projectQueryService.findPublishedBySlug(slug);
        return projectDtoMapper.toResponse(project);
    }
}