package com.example.ghcarvalho.portfolio.project.infra.controller;

import com.example.ghcarvalho.portfolio.project.application.dto.project.CreateProjectDto;
import com.example.ghcarvalho.portfolio.project.application.dto.project.ProjectDto;
import com.example.ghcarvalho.portfolio.project.application.dto.project.ProjectDtoMapper;
import com.example.ghcarvalho.portfolio.project.application.dto.project.UpdateProjectDto;
import com.example.ghcarvalho.portfolio.project.application.query.ProjectQueryService;
import com.example.ghcarvalho.portfolio.project.application.service.ProjectCommandService;
import com.example.ghcarvalho.portfolio.project.domain.project.Project;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/projects")
public class AdminProjectController {

    private final ProjectCommandService projectCommandService;
    private final ProjectQueryService projectQueryService;
    private final ProjectDtoMapper projectDtoMapper;

    public AdminProjectController(
            ProjectCommandService projectCommandService,
            ProjectQueryService projectQueryService,
            ProjectDtoMapper projectDtoMapper
    ) {
        this.projectCommandService = projectCommandService;
        this.projectQueryService = projectQueryService;
        this.projectDtoMapper = projectDtoMapper;
    }

    @GetMapping
    public List<ProjectDto> findAll() {
        return projectQueryService.findAll()
                .stream()
                .map(projectDtoMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ProjectDto findById(@PathVariable UUID id) {
        Project project = projectQueryService.findById(id);
        return projectDtoMapper.toResponse(project);
    }

    @PostMapping
    public ResponseEntity<ProjectDto> create(
            @Valid @RequestBody CreateProjectDto request
    ) {
        Project project = projectCommandService.create(
                projectDtoMapper.toCommand(request)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectDtoMapper.toResponse(project));
    }

    @PutMapping("/{id}")
    public ProjectDto update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectDto request
    ) {
        Project project = projectCommandService.update(
                projectDtoMapper.toCommand(id, request)
        );

        return projectDtoMapper.toResponse(project);
    }

    @PatchMapping("/{id}/publish")
    public ProjectDto publish(@PathVariable UUID id) {
        Project project = projectCommandService.publish(id);
        return projectDtoMapper.toResponse(project);
    }

    @PatchMapping("/{id}/unpublish")
    public ProjectDto unpublish(@PathVariable UUID id) {
        Project project = projectCommandService.unpublish(id);
        return projectDtoMapper.toResponse(project);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        projectCommandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}