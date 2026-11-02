package com.example.ghcarvalho.portfolio.project.infra.controller.technology;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ghcarvalho.portfolio.project.application.dto.technology.TechnologyResponseDto;
import com.example.ghcarvalho.portfolio.project.application.service.technology.TechnologyService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/technologies")
public class PublicTechnologyController {

    private final TechnologyService technologyService;

    public PublicTechnologyController(TechnologyService technologyService) {
        this.technologyService = technologyService;
    }

    @GetMapping
    public List<TechnologyResponseDto> findAll() {
        return technologyService.findAll();
    }
}

