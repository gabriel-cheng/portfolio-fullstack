package com.example.ghcarvalho.portfolio.project.application.dto.company;

import java.util.UUID;

public record CompanySummaryDto(
        UUID id,
        String name,
        String slug,
        String logoUrl,
        String website
) {
}

