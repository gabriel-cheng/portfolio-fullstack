package com.example.ghcarvalho.portfolio.project.infra.controller.experience;

import com.example.ghcarvalho.portfolio.project.application.dto.experience.ExperienceRequestDto;
import com.example.ghcarvalho.portfolio.project.application.dto.experience.ExperienceResponseDto;
import com.example.ghcarvalho.portfolio.project.application.service.experience.ExperienceService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/experiences")
public class AdminExperienceController {

    private final ExperienceService experienceService;

    public AdminExperienceController(
            ExperienceService experienceService
    ) {
        this.experienceService = experienceService;
    }

    @GetMapping
    public List<ExperienceResponseDto> findAll(
            @RequestParam(required = false) UUID companyId
    ) {
        if (companyId != null) {
            return experienceService.findAllByCompanyId(companyId);
        }
        return experienceService.findAll();
    }

    @GetMapping("/{id}")
    public ExperienceResponseDto findById(
            @PathVariable UUID id
    ) {
        return experienceService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ExperienceResponseDto> create(
            @Valid @RequestBody ExperienceRequestDto request
    ) {
        ExperienceResponseDto response =
                experienceService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public ExperienceResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody ExperienceRequestDto request
    ) {
        return experienceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        experienceService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
