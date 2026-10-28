package com.example.ghcarvalho.portfolio.project.application.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record UpdateProjectDto(
        @NotBlank
        @Size(max = 200)
        String title,

        @NotBlank
        @Size(max = 200)
        String slug,

        @NotBlank
        @Size(max = 500)
        String summary,

        @NotNull
        Map<String, Object> content,

        boolean featured,

        @PositiveOrZero
        int displayOrder
) {
}