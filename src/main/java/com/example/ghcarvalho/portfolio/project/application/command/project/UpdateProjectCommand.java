package com.example.ghcarvalho.portfolio.project.application.command.project;

import java.util.List;
import java.util.UUID;

public record UpdateProjectCommand(
        UUID id,
        String number,
        String title,
        String slug,
        String category,
        String status,
        String description,
        List<String> highlights,
        List<String> technologies,
        String liveUrl,
        String githubUrl,
        String image,
        boolean featured,
        int displayOrder
) {
}
