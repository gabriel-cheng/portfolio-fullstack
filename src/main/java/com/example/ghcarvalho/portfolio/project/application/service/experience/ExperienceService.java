package com.example.ghcarvalho.portfolio.project.application.service.experience;

import com.example.ghcarvalho.portfolio.project.application.dto.company.CompanySummaryDto;
import com.example.ghcarvalho.portfolio.project.application.dto.experience.ExperienceResponseDto;
import com.example.ghcarvalho.portfolio.project.application.dto.experience.ExperienceRequestDto;
import com.example.ghcarvalho.portfolio.project.application.service.company.CompanyService;
import com.example.ghcarvalho.portfolio.project.domain.company.Company;
import com.example.ghcarvalho.portfolio.project.domain.experience.Experience;
import com.example.ghcarvalho.portfolio.project.domain.experience.ExperienceRepository;
import com.example.ghcarvalho.portfolio.project.domain.experience.ExperienceStatus;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final CompanyService companyService;

    public ExperienceService(
            ExperienceRepository experienceRepository,
            CompanyService companyService
    ) {
        this.experienceRepository = experienceRepository;
        this.companyService = companyService;
    }

    @Transactional
    public ExperienceResponseDto create(ExperienceRequestDto request) {
        if (experienceRepository.existsBySlug(request.slug())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma experiência com esse slug."
            );
        }

        Company company = resolveCompany(request, null);

        OffsetDateTime now = OffsetDateTime.now();

        Experience experience = toDomain(
                UUID.randomUUID(),
                request,
                company,
                ExperienceStatus.PUBLISHED,
                now,
                now
        );

        return toResponse(experienceRepository.save(experience));
    }

    @Transactional
    public ExperienceResponseDto update(UUID id, ExperienceRequestDto request) {
        Experience existing = findByIdOrThrow(id);

        if (experienceRepository.existsBySlugAndIdNot(
                request.slug(),
                id
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma experiência com esse slug."
            );
        }

        Company company = resolveCompany(request, existing.getCompanyId());

        Experience updated = toDomain(
                existing.getId(),
                request,
                company,
                existing.getStatus(),
                existing.getCreatedAt(),
                OffsetDateTime.now()
        );

        return toResponse(experienceRepository.save(updated));
    }

    @Transactional(readOnly = true)
    public List<ExperienceResponseDto> findAll() {
        return experienceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExperienceResponseDto> findAllPublished() {
        return experienceRepository.findAllPublished()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExperienceResponseDto findPublishedBySlug(String slug) {
        Experience experience = experienceRepository.findBySlug(slug)
                .filter(item ->
                        item.getStatus() == ExperienceStatus.PUBLISHED)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Experiência não encontrada."
                ));

        return toResponse(experience);
    }

    @Transactional(readOnly = true)
    public ExperienceResponseDto findById(UUID id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ExperienceResponseDto> findAllByCompanyId(UUID companyId) {
        return experienceRepository.findAllByCompanyId(companyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExperienceResponseDto> findAllPublishedByCompanyId(UUID companyId) {
        return experienceRepository.findAllPublishedByCompanyId(companyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {
        findByIdOrThrow(id);
        experienceRepository.deleteById(id);
    }

    @Transactional
    public ExperienceResponseDto setVisibility(UUID id, boolean visible) {
        Experience existing = findByIdOrThrow(id);

        ExperienceStatus status = visible
                ? ExperienceStatus.PUBLISHED
                : ExperienceStatus.HIDDEN;

        Experience updated = new Experience(
                existing.getId(),
                existing.getSlug(),
                existing.getCompanyId(),
                existing.getCompany(),
                existing.getRole(),
                existing.getEmploymentType(),
                existing.getLocation(),
                existing.getDescription(),
                existing.getStartDate(),
                existing.getEndDate(),
                existing.isCurrent(),
                existing.getDisplayOrder(),
                status,
                existing.getTechnologies(),
                existing.getCreatedAt(),
                OffsetDateTime.now()
        );

        return toResponse(experienceRepository.save(updated));
    }

    private Company resolveCompany(ExperienceRequestDto request, UUID fallbackCompanyId) {
        if (request.companyId() != null) {
            return companyService.findByIdOrThrow(request.companyId());
        }

        if (request.company() != null && !request.company().isBlank()) {
            return companyService.findOrCreateByName(request.company());
        }

        if (fallbackCompanyId != null) {
            return companyService.findByIdOrThrow(fallbackCompanyId);
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A empresa (companyId ou nome) é obrigatória."
        );
    }

    private Experience findByIdOrThrow(UUID id) {
        return experienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Experiência não encontrada."
                ));
    }

    private Experience toDomain(
            UUID id,
            ExperienceRequestDto request,
            Company company,
            ExperienceStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new Experience(
                id,
                request.slug(),
                company.getId(),
                company,
                request.role(),
                request.employmentType(),
                request.location(),
                request.description(),
                request.startDate(),
                request.endDate(),
                request.current(),
                request.displayOrder(),
                status,
                request.technologies(),
                createdAt,
                updatedAt
        );
    }

    private ExperienceResponseDto toResponse(Experience experience) {
        Company company = experience.getCompany();
        if (company == null && experience.getCompanyId() != null) {
            try {
                company = companyService.findByIdOrThrow(experience.getCompanyId());
            } catch (Exception ignored) {
                // Ignore if not resolvable immediately
            }
        }

        String companyName = company != null ? company.getName() : null;
        CompanySummaryDto companySummary = companyService.toSummary(company);

        return new ExperienceResponseDto(
                experience.getId(),
                experience.getSlug(),
                experience.getCompanyId(),
                companyName,
                companySummary,
                experience.getRole(),
                experience.getEmploymentType(),
                experience.getLocation(),
                experience.getDescription(),
                experience.getStartDate(),
                experience.getEndDate(),
                experience.isCurrent(),
                experience.getDisplayOrder(),
                experience.getStatus(),
                experience.getTechnologies(),
                experience.getCreatedAt(),
                experience.getUpdatedAt()
        );
    }
}
