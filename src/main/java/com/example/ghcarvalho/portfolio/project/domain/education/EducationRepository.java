package com.example.ghcarvalho.portfolio.project.domain.education;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EducationRepository {

    Education save(Education education);

    Optional<Education> findById(UUID id);

    List<Education> findAll();

    void deleteById(UUID id);
}

