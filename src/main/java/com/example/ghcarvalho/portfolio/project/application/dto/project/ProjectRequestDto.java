package com.example.ghcarvalho.portfolio.project.application.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.JsonNode;

public record ProjectRequestDto(

        @NotBlank
        @Size(max = 180)
        String title,

        @NotBlank
        @Size(max = 180)
        String slug,

        @NotBlank
        @Size(max = 500)
        String summary,

        @NotNull
        JsonNode content,

        boolean featured,

        @PositiveOrZero
        int displayOrder
) {
}