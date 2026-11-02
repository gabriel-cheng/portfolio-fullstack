CREATE TABLE educations (
    id UUID PRIMARY KEY,
    degree VARCHAR(180) NOT NULL,
    institution VARCHAR(180) NOT NULL,
    start_year VARCHAR(10) NOT NULL,
    end_year VARCHAR(10),
    status VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    diploma_image VARCHAR(500),
    diploma_alt VARCHAR(255),
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_educations_display_order
    ON educations (display_order ASC, start_year DESC);

CREATE TABLE education_skills (
    education_id UUID NOT NULL,
    skill VARCHAR(100) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT pk_education_skills
        PRIMARY KEY (education_id, skill),

    CONSTRAINT fk_education_skills_education
        FOREIGN KEY (education_id)
        REFERENCES educations (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_education_skills_order
    ON education_skills (education_id, display_order);

