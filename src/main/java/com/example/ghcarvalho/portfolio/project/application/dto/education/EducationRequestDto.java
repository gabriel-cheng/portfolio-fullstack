package com.example.ghcarvalho.portfolio.project.application.dto.education;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record EducationRequestDto(
        @NotBlank(message = "O curso/grau não pode ser vazio.")
        @Size(max = 180, message = "O curso/grau deve ter no máximo 180 caracteres.")
        String degree,

        @NotBlank(message = "A instituição não pode ser vazia.")
        @Size(max = 180, message = "A instituição deve ter no máximo 180 caracteres.")
        String institution,

        @NotBlank(message = "O ano de início não pode ser vazio.")
        @Size(max = 10, message = "O ano de início deve ter no máximo 10 caracteres.")
        String startYear,

        @Size(max = 10, message = "O ano de conclusão deve ter no máximo 10 caracteres.")
        String endYear,

        @NotBlank(message = "O status não pode ser vazio.")
        @Size(max = 50, message = "O status deve ter no máximo 50 caracteres.")
        String status,

        @NotBlank(message = "O tipo não pode ser vazio.")
        @Size(max = 50, message = "O tipo deve ter no máximo 50 caracteres.")
        String type,

        String description,

        List<String> skills,

        @Size(max = 500, message = "O caminho da imagem do diploma deve ter no máximo 500 caracteres.")
        String diplomaImage,

        @Size(max = 255, message = "O texto alternativo do diploma deve ter no máximo 255 caracteres.")
        String diplomaAlt,

        Integer displayOrder
) {
}

