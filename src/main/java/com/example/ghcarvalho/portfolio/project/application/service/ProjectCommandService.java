package com.example.ghcarvalho.portfolio.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ghcarvalho.portfolio.project.application.command.project.CreateProjectCommand;
import com.example.ghcarvalho.portfolio.project.application.command.project.UpdateProjectCommand;
import com.example.ghcarvalho.portfolio.project.application.exceptions.ProjectNotFoundException;
import com.example.ghcarvalho.portfolio.project.application.exceptions.ProjectSlugAlreadyExistsException;
import com.example.ghcarvalho.portfolio.project.domain.project.Project;
import com.example.ghcarvalho.portfolio.project.domain.project.ProjectRepository;

import java.util.UUID;

@Service
public class ProjectCommandService {

    private final ProjectRepository projectRepository;

    public ProjectCommandService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Project create(CreateProjectCommand command) {
        String slug = command.slug().trim();

        if (projectRepository.existsBySlug(slug)) {
            throw new ProjectSlugAlreadyExistsException(slug);
        }

        Project project = Project.create(
                command.title(),
                command.slug(),
                command.summary(),
                command.content(),
                command.featured(),
                command.displayOrder()
        );

        return projectRepository.save(project);
    }

    @Transactional
    public Project update(UpdateProjectCommand command) {
        Project project = findRequired(command.id());
        String slug = command.slug().trim();

        if (projectRepository.existsBySlugAndIdNot(
                slug,
                command.id()
        )) {
            throw new ProjectSlugAlreadyExistsException(slug);
        }

        project.update(
                command.title(),
                command.slug(),
                command.summary(),
                command.content(),
                command.featured(),
                command.displayOrder()
        );

        return projectRepository.save(project);
    }

    @Transactional
    public Project publish(UUID id) {
        Project project = findRequired(id);
        project.publish();

        return projectRepository.save(project);
    }

    @Transactional
    public Project unpublish(UUID id) {
        Project project = findRequired(id);
        project.unpublish();

        return projectRepository.save(project);
    }

    @Transactional
    public void delete(UUID id) {
        findRequired(id);
        projectRepository.deleteById(id);
    }

    private Project findRequired(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }
}