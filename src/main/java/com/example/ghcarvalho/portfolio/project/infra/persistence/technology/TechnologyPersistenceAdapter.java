package com.example.ghcarvalho.portfolio.project.infra.persistence.technology;

import org.springframework.stereotype.Repository;

import com.example.ghcarvalho.portfolio.project.domain.technology.Technology;
import com.example.ghcarvalho.portfolio.project.domain.technology.TechnologyRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TechnologyPersistenceAdapter implements TechnologyRepository {

    private final TechnologyJpaRepository jpaRepository;

    public TechnologyPersistenceAdapter(TechnologyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Technology save(Technology technology) {
        TechnologyEntity entity = toEntity(technology);
        TechnologyEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Technology> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Technology> findByNameIgnoreCase(String name) {
        return jpaRepository.findByNameIgnoreCase(name).map(this::toDomain);
    }

    @Override
    public Optional<Technology> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toDomain);
    }

    @Override
    public List<Technology> findAll() {
        return jpaRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }

    private TechnologyEntity toEntity(Technology domain) {
        return new TechnologyEntity(
                domain.getId(),
                domain.getName(),
                domain.getSlug(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    private Technology toDomain(TechnologyEntity entity) {
        return Technology.restore(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

