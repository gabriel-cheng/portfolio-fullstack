package com.example.ghcarvalho.portfolio.project.application.dto.project;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.example.ghcarvalho.portfolio.project.domain.company.PublicationStatus;

public record ProjectResponseDto(
        UUID id,
        String number,
        String title,
        String slug,
        String category,
        String status,
        PublicationStatus publicationStatus,
        String description,
        List<String> highlights,
        List<String> technologies,
        String liveUrl,
        String githubUrl,
        String image,
        boolean featured,
        int displayOrder,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
