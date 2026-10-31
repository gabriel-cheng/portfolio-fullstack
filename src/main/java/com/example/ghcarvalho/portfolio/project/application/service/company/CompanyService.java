package com.example.ghcarvalho.portfolio.project.application.service.company;

import com.example.ghcarvalho.portfolio.project.application.dto.company.CompanyRequestDto;
import com.example.ghcarvalho.portfolio.project.application.dto.company.CompanyResponseDto;
import com.example.ghcarvalho.portfolio.project.application.dto.company.CompanySummaryDto;
import com.example.ghcarvalho.portfolio.project.application.dto.experience.ExperienceResponseDto;
import com.example.ghcarvalho.portfolio.project.domain.company.Company;
import com.example.ghcarvalho.portfolio.project.domain.company.CompanyRepository;
import com.example.ghcarvalho.portfolio.project.domain.company.PublicationStatus;
import com.example.ghcarvalho.portfolio.project.domain.experience.Experience;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class CompanyService {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9]+");

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional
    public CompanyResponseDto create(CompanyRequestDto request) {
        if (companyRepository.existsBySlug(request.slug())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma empresa com esse slug."
            );
        }

        OffsetDateTime now = OffsetDateTime.now();

        Company company = new Company(
                UUID.randomUUID(),
                request.name(),
                request.slug(),
                request.description(),
                request.website(),
                request.logoUrl(),
                PublicationStatus.DRAFT,
                List.of(),
                now,
                now
        );

        return toResponse(companyRepository.save(company));
    }

    @Transactional
    public CompanyResponseDto update(UUID id, CompanyRequestDto request) {
        Company existing = findByIdOrThrow(id);

        if (companyRepository.existsBySlugAndIdNot(request.slug(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma empresa com esse slug."
            );
        }

        Company updated = new Company(
                existing.getId(),
                request.name(),
                request.slug(),
                request.description(),
                request.website(),
                request.logoUrl(),
                existing.getStatus(),
                existing.getExperiences(),
                existing.getCreatedAt(),
                OffsetDateTime.now()
        );

        return toResponse(companyRepository.save(updated));
    }

    @Transactional
    public CompanyResponseDto publish(UUID id) {
        Company existing = findByIdOrThrow(id);
        Company published = existing.publish();
        return toResponse(companyRepository.save(published));
    }

    @Transactional
    public CompanyResponseDto unpublish(UUID id) {
        Company existing = findByIdOrThrow(id);
        Company unpublished = existing.unpublish();
        return toResponse(companyRepository.save(unpublished));
    }

    @Transactional(readOnly = true)
    public List<CompanyResponseDto> findAll() {
        return companyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompanyResponseDto> findAllPublished() {
        return companyRepository.findAllPublished()
                .stream()
                .filter(company -> !company.getExperiences().isEmpty())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyResponseDto findById(UUID id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public CompanyResponseDto findBySlug(String slug) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empresa não encontrada."
                ));

        return toResponse(company);
    }

    @Transactional(readOnly = true)
    public CompanyResponseDto findPublishedBySlug(String slug) {
        Company company = companyRepository.findPublishedBySlug(slug)
                .filter(c -> !c.getExperiences().isEmpty())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empresa não encontrada."
                ));

        return toResponse(company);
    }

    @Transactional
    public void delete(UUID id) {
        findByIdOrThrow(id);
        companyRepository.deleteById(id);
    }

    @Transactional
    public Company findOrCreateByName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da empresa não pode ser vazio."
            );
        }

        String normalizedName = name.trim();

        return companyRepository.findByName(normalizedName)
                .orElseGet(() -> {
                    String baseSlug = generateSlug(normalizedName);
                    String slug = baseSlug;
                    int counter = 1;

                    while (companyRepository.existsBySlug(slug)) {
                        slug = baseSlug + "-" + counter++;
                    }

                    OffsetDateTime now = OffsetDateTime.now();
                    Company newCompany = new Company(
                            UUID.randomUUID(),
                            normalizedName,
                            slug,
                            null,
                            null,
                            null,
                            PublicationStatus.DRAFT,
                            List.of(),
                            now,
                            now
                    );

                    return companyRepository.save(newCompany);
                });
    }

    public Company findByIdOrThrow(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empresa não encontrada."
                ));
    }

    public CompanyResponseDto toResponse(Company company) {
        List<ExperienceResponseDto> experiences = company.getExperiences() == null
                ? List.of()
                : company.getExperiences().stream()
                .map(this::toExperienceResponse)
                .toList();

        return new CompanyResponseDto(
                company.getId(),
                company.getName(),
                company.getSlug(),
                company.getDescription(),
                company.getWebsite(),
                company.getLogoUrl(),
                company.getStatus(),
                experiences,
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }

    public CompanySummaryDto toSummary(Company company) {
        if (company == null) {
            return null;
        }

        return new CompanySummaryDto(
                company.getId(),
                company.getName(),
                company.getSlug(),
                company.getLogoUrl(),
                company.getWebsite(),
                company.getStatus()
        );
    }

    private ExperienceResponseDto toExperienceResponse(Experience experience) {
        CompanySummaryDto summary = experience.getCompany() != null
                ? toSummary(experience.getCompany())
                : null;

        String companyName = experience.getCompany() != null
                ? experience.getCompany().getName()
                : null;

        return new ExperienceResponseDto(
                experience.getId(),
                experience.getSlug(),
                experience.getCompanyId(),
                companyName,
                summary,
                experience.getRole(),
                experience.getEmploymentType(),
                experience.getLocation(),
                experience.getDescription(),
                experience.getStartDate(),
                experience.getEndDate(),
                experience.isCurrent(),
                experience.getDisplayOrder(),
                experience.getTechnologies(),
                experience.getCreatedAt(),
                experience.getUpdatedAt()
        );
    }

    private String generateSlug(String text) {
        String slug = NON_ALPHANUMERIC.matcher(text.toLowerCase()).replaceAll("-");
        slug = slug.replaceAll("^-+|-+$", "");
        return slug.isBlank() ? "empresa" : slug;
    }
}
