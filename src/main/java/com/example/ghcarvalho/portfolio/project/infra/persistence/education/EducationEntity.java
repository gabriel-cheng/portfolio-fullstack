package com.example.ghcarvalho.portfolio.project.infra.persistence.education;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "educations")
public class EducationEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 180)
    private String degree;

    @Column(nullable = false, length = 180)
    private String institution;

    @Column(name = "start_year", nullable = false, length = 10)
    private String startYear;

    @Column(name = "end_year", length = 10)
    private String endYear;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "education_skills",
            joinColumns = @JoinColumn(name = "education_id")
    )
    @Column(name = "skill", nullable = false, length = 100)
    @OrderColumn(name = "display_order")
    private List<String> skills = new ArrayList<>();

    @Column(name = "diploma_image", length = 500)
    private String diplomaImage;

    @Column(name = "diploma_alt", length = 255)
    private String diplomaAlt;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public EducationEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getEndYear() {
        return endYear;
    }

    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        if (this.skills == null) {
            this.skills = new ArrayList<>();
        } else {
            this.skills.clear();
        }
        if (skills != null) {
            this.skills.addAll(skills);
        }
    }

    public String getDiplomaImage() {
        return diplomaImage;
    }

    public void setDiplomaImage(String diplomaImage) {
        this.diplomaImage = diplomaImage;
    }

    public String getDiplomaAlt() {
        return diplomaAlt;
    }

    public void setDiplomaAlt(String diplomaAlt) {
        this.diplomaAlt = diplomaAlt;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

