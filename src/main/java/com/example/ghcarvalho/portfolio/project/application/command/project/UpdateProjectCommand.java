package com.example.ghcarvalho.portfolio.project.application.command.project;

import java.util.Map;
import java.util.UUID;

public record UpdateProjectCommand(
        UUID id,
        String title,
        String slug,
        String summary,
        Map<String, Object> content,
        boolean featured,
        int displayOrder
) {
}