package com.example.ghcarvalho.portfolio.project.application.dto.experience;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.example.ghcarvalho.portfolio.project.domain.experience.EmploymentType;

public record ExperienceRequestDto(

        @NotBlank
        @Size(max = 180)
        String slug,

        UUID companyId,

        @Size(max = 180)
        String company,

        @NotBlank
        @Size(max = 180)
        String role,

        @NotNull
        EmploymentType employmentType,

        @Size(max = 180)
        String location,

        @NotBlank
        String description,

        @NotNull
        LocalDate startDate,

        LocalDate endDate,

        boolean current,

        @PositiveOrZero
        int displayOrder,

        List<@NotBlank @Size(max = 100) String> technologies
) {
}
