package com.example.ghcarvalho.portfolio.project.infra.controller.experience;

import com.example.ghcarvalho.portfolio.project.application.dto.experience.ExperienceResponseDto;
import com.example.ghcarvalho.portfolio.project.application.service.experience.ExperienceService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/experiences")
public class PublicExperienceController {

    private final ExperienceService experienceService;

    public PublicExperienceController(
            ExperienceService experienceService
    ) {
        this.experienceService = experienceService;
    }

    @GetMapping
    public List<ExperienceResponseDto> findAllPublished() {
        return experienceService.findAllPublished();
    }

    @GetMapping("/{slug}")
    public ExperienceResponseDto findPublishedBySlug(
            @PathVariable String slug
    ) {
        return experienceService.findPublishedBySlug(slug);
    }
}