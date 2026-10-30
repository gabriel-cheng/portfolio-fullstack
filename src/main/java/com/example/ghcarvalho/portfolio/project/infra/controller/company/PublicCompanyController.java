package com.example.ghcarvalho.portfolio.project.infra.controller.company;

import com.example.ghcarvalho.portfolio.project.application.dto.company.CompanyResponseDto;
import com.example.ghcarvalho.portfolio.project.application.service.company.CompanyService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/companies")
public class PublicCompanyController {

    private final CompanyService companyService;

    public PublicCompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public List<CompanyResponseDto> findAll() {
        return companyService.findAll();
    }

    @GetMapping("/{slug}")
    public CompanyResponseDto findBySlug(@PathVariable String slug) {
        return companyService.findBySlug(slug);
    }
}

