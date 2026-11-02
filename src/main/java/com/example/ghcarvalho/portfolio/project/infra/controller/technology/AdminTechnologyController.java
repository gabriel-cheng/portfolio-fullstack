package com.example.ghcarvalho.portfolio.project.infra.controller.technology;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ghcarvalho.portfolio.project.application.dto.technology.TechnologyRequestDto;
import com.example.ghcarvalho.portfolio.project.application.dto.technology.TechnologyResponseDto;
import com.example.ghcarvalho.portfolio.project.application.service.technology.TechnologyService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/technologies")
public class AdminTechnologyController {

    private final TechnologyService technologyService;

    public AdminTechnologyController(TechnologyService technologyService) {
        this.technologyService = technologyService;
    }

    @GetMapping
    public List<TechnologyResponseDto> findAll() {
        return technologyService.findAll();
    }

    @PostMapping
    public ResponseEntity<TechnologyResponseDto> create(
            @Valid @RequestBody TechnologyRequestDto request
    ) {
        TechnologyResponseDto response = technologyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        technologyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

