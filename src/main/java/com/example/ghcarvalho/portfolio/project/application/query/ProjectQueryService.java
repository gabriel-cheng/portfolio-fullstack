package com.example.ghcarvalho.portfolio.project.application.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ghcarvalho.portfolio.project.application.exceptions.ProjectNotFoundException;
import com.example.ghcarvalho.portfolio.project.domain.project.Project;
import com.example.ghcarvalho.portfolio.project.domain.project.ProjectRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProjectQueryService {

    private final ProjectRepository projectRepository;

    public ProjectQueryService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project findById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    public List<Project> findAllPublished() {
        return projectRepository.findAllPublished();
    }

    public Project findPublishedBySlug(String slug) {
        return projectRepository.findPublishedBySlug(slug)
                .orElseThrow(() -> new ProjectNotFoundException(
                        "Projeto publicado não encontrado para o slug: " + slug
                ));
    }
}