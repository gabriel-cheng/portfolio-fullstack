package com.example.ghcarvalho.portfolio.project.application.service.education;

import com.example.ghcarvalho.portfolio.project.application.dto.education.EducationRequestDto;
import com.example.ghcarvalho.portfolio.project.application.dto.education.EducationResponseDto;
import com.example.ghcarvalho.portfolio.project.application.dto.education.PublicEducationResponseDto;
import com.example.ghcarvalho.portfolio.project.domain.education.Education;
import com.example.ghcarvalho.portfolio.project.domain.education.EducationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EducationService {

    private final EducationRepository educationRepository;

    public EducationService(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    @Transactional
    public EducationResponseDto create(EducationRequestDto request) {
        OffsetDateTime now = OffsetDateTime.now();

        Education education = new Education(
                UUID.randomUUID(),
                request.degree(),
                request.institution(),
                request.startYear(),
                request.endYear(),
                request.status(),
                request.type(),
                request.description(),
                request.skills(),
                request.diplomaImage(),
                request.diplomaAlt(),
                request.displayOrder() != null ? request.displayOrder() : 0,
                now,
                now
        );

        return toResponse(educationRepository.save(education));
    }

    @Transactional
    public EducationResponseDto update(UUID id, EducationRequestDto request) {
        Education existing = findByIdOrThrow(id);

        Education updated = new Education(
                existing.getId(),
                request.degree(),
                request.institution(),
                request.startYear(),
                request.endYear(),
                request.status(),
                request.type(),
                request.description(),
                request.skills(),
                request.diplomaImage(),
                request.diplomaAlt(),
                request.displayOrder() != null ? request.displayOrder() : existing.getDisplayOrder(),
                existing.getCreatedAt(),
                OffsetDateTime.now()
        );

        return toResponse(educationRepository.save(updated));
    }

    @Transactional(readOnly = true)
    public List<EducationResponseDto> findAll() {
        return educationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PublicEducationResponseDto> findAllPublic() {
        return educationRepository.findAll()
                .stream()
                .map(this::toPublicResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EducationResponseDto findById(UUID id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PublicEducationResponseDto findPublicById(UUID id) {
        return toPublicResponse(findByIdOrThrow(id));
    }

    @Transactional
    public void delete(UUID id) {
        findByIdOrThrow(id);
        educationRepository.deleteById(id);
    }

    private Education findByIdOrThrow(UUID id) {
        return educationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Formação acadêmica não encontrada."
                ));
    }

    private EducationResponseDto toResponse(Education education) {
        return new EducationResponseDto(
                education.getId(),
                education.getDegree(),
                education.getInstitution(),
                education.getStartYear(),
                education.getEndYear(),
                education.getStatus(),
                education.getType(),
                education.getDescription(),
                education.getSkills(),
                education.getDiplomaImage(),
                education.getDiplomaAlt(),
                education.getDisplayOrder(),
                education.getCreatedAt(),
                education.getUpdatedAt()
        );
    }

    private PublicEducationResponseDto toPublicResponse(Education education) {
        return new PublicEducationResponseDto(
                education.getId(),
                education.getDegree(),
                education.getInstitution(),
                education.getStartYear(),
                education.getEndYear(),
                education.getStatus(),
                education.getType(),
                education.getDescription(),
                education.getSkills(),
                education.getDiplomaImage(),
                education.getDiplomaAlt()
        );
    }
}

