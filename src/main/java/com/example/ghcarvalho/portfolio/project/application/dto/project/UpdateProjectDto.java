package com.example.ghcarvalho.portfolio.project.application.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateProjectDto(
        @Size(max = 10)
        String number,

        @NotBlank(message = "O título é obrigatório.")
        @Size(max = 200)
        String title,

        @NotBlank(message = "O slug é obrigatório.")
        @Size(max = 200)
        String slug,

        @NotBlank(message = "A categoria é obrigatória.")
        @Size(max = 100)
        String category,

        @NotBlank(message = "O status é obrigatório.")
        @Size(max = 100)
        String status,

        @NotBlank(message = "A descrição é obrigatória.")
        String description,

        List<String> highlights,

        List<String> technologies,

        @Size(max = 500)
        String liveUrl,

        @Size(max = 500)
        String githubUrl,

        @Size(max = 500)
        String image,

        boolean featured,

        @PositiveOrZero
        int displayOrder
) {
}
