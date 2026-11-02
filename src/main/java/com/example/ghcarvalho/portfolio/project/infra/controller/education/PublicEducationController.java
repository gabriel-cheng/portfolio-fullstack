package com.example.ghcarvalho.portfolio.project.infra.controller.education;

import com.example.ghcarvalho.portfolio.project.application.dto.education.PublicEducationResponseDto;
import com.example.ghcarvalho.portfolio.project.application.service.education.EducationService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/educations")
public class PublicEducationController {

    private final EducationService educationService;

    public PublicEducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @GetMapping
    public List<PublicEducationResponseDto> findAll() {
        return educationService.findAllPublic();
    }

    @GetMapping("/{id}")
    public PublicEducationResponseDto findById(@PathVariable UUID id) {
        return educationService.findPublicById(id);
    }
}

