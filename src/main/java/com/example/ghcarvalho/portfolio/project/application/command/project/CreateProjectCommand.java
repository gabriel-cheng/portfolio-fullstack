package com.example.ghcarvalho.portfolio.project.application.command.project;

import java.util.Map;

public record CreateProjectCommand(
        String title,
        String slug,
        String summary,
        Map<String, Object> content,
        boolean featured,
        int displayOrder
) {
}