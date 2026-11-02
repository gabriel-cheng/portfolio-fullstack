package com.example.ghcarvalho.portfolio.project.application.service.technology;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ghcarvalho.portfolio.project.application.dto.technology.TechnologyRequestDto;
import com.example.ghcarvalho.portfolio.project.application.dto.technology.TechnologyResponseDto;
import com.example.ghcarvalho.portfolio.project.domain.technology.Technology;
import com.example.ghcarvalho.portfolio.project.domain.technology.TechnologyRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TechnologyService {

    private final TechnologyRepository technologyRepository;

    public TechnologyService(TechnologyRepository technologyRepository) {
        this.technologyRepository = technologyRepository;
    }

    @Transactional(readOnly = true)
    public List<TechnologyResponseDto> findAll() {
        return technologyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TechnologyResponseDto findOrCreate(String name) {
        String trimmedName = name.trim();
        Optional<Technology> existing = technologyRepository.findByNameIgnoreCase(trimmedName);
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        Technology newTech = Technology.create(trimmedName);
        Technology saved = technologyRepository.save(newTech);
        return toResponse(saved);
    }

    @Transactional
    public TechnologyResponseDto create(TechnologyRequestDto request) {
        return findOrCreate(request.name());
    }

    @Transactional
    public void delete(UUID id) {
        technologyRepository.deleteById(id);
    }

    private TechnologyResponseDto toResponse(Technology domain) {
        return new TechnologyResponseDto(
                domain.getId(),
                domain.getName(),
                domain.getSlug(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}

