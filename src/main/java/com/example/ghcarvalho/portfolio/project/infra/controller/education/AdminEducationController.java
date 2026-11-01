package com.example.ghcarvalho.portfolio.project.infra.controller.education;

import com.example.ghcarvalho.portfolio.project.application.dto.education.EducationRequestDto;
import com.example.ghcarvalho.portfolio.project.application.dto.education.EducationResponseDto;
import com.example.ghcarvalho.portfolio.project.application.service.education.EducationService;

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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/educations")
public class AdminEducationController {

    private final EducationService educationService;

    public AdminEducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @GetMapping
    public List<EducationResponseDto> findAll() {
        return educationService.findAll();
    }

    @GetMapping("/{id}")
    public EducationResponseDto findById(@PathVariable UUID id) {
        return educationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<EducationResponseDto> create(
            @Valid @RequestBody EducationRequestDto request
    ) {
        EducationResponseDto response = educationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public EducationResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody EducationRequestDto request
    ) {
        return educationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        educationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

