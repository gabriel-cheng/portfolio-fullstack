package com.example.ghcarvalho.portfolio.project.infra.persistence.education;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EducationJpaRepository extends JpaRepository<EducationEntity, UUID> {

    List<EducationEntity> findAllByOrderByDisplayOrderAscStartYearDesc();
}

