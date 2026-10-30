package com.example.ghcarvalho.portfolio.project.infra.controller.company;

import com.example.ghcarvalho.portfolio.project.application.dto.company.CompanyRequestDto;
import com.example.ghcarvalho.portfolio.project.application.dto.company.CompanyResponseDto;
import com.example.ghcarvalho.portfolio.project.application.service.company.CompanyService;

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
@RequestMapping("/api/v1/admin/companies")
public class AdminCompanyController {

    private final CompanyService companyService;

    public AdminCompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public List<CompanyResponseDto> findAll() {
        return companyService.findAll();
    }

    @GetMapping("/{id}")
    public CompanyResponseDto findById(@PathVariable UUID id) {
        return companyService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CompanyResponseDto> create(
            @Valid @RequestBody CompanyRequestDto request
    ) {
        CompanyResponseDto response = companyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public CompanyResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyRequestDto request
    ) {
        return companyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

