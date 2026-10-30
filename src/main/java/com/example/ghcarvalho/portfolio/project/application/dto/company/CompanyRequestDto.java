package com.example.ghcarvalho.portfolio.project.application.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequestDto(

        @NotBlank(message = "O nome da empresa é obrigatório.")
        @Size(max = 180, message = "O nome da empresa deve ter no máximo 180 caracteres.")
        String name,

        @NotBlank(message = "O slug da empresa é obrigatório.")
        @Size(max = 180, message = "O slug da empresa deve ter no máximo 180 caracteres.")
        String slug,

        String description,

        @Size(max = 255, message = "O website deve ter no máximo 255 caracteres.")
        String website,

        @Size(max = 500, message = "O logo_url deve ter no máximo 500 caracteres.")
        String logoUrl
) {
}

