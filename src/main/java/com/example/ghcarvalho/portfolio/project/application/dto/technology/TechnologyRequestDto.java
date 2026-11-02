package com.example.ghcarvalho.portfolio.project.application.dto.technology;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TechnologyRequestDto(
        @NotBlank(message = "O nome da tecnologia é obrigatório.")
        @Size(max = 100, message = "O nome da tecnologia não pode ultrapassar 100 caracteres.")
        String name
) {
}

