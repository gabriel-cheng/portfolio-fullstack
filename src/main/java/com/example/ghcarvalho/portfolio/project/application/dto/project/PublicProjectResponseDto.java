package com.example.ghcarvalho.portfolio.project.application.dto.project;

import java.util.List;

public record PublicProjectResponseDto(
        String number,
        String title,
        String category,
        String status,
        String description,
        List<String> highlights,
        List<String> technologies,
        String liveUrl,
        String githubUrl,
        String image
) {
}

