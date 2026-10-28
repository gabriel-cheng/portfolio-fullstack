CREATE TABLE experiences (
    id UUID PRIMARY KEY,
    slug VARCHAR(180) NOT NULL UNIQUE,
    company VARCHAR(180) NOT NULL,
    role VARCHAR(180) NOT NULL,
    employment_type VARCHAR(40) NOT NULL,
    location VARCHAR(180),
    description TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    current BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_experiences_dates
        CHECK (end_date IS NULL OR end_date >= start_date),

    CONSTRAINT chk_experiences_current
        CHECK (NOT current OR end_date IS NULL),

    CONSTRAINT chk_experiences_status
        CHECK (status IN ('PUBLISHED', 'HIDDEN')),

    CONSTRAINT chk_experiences_employment_type
        CHECK (
            employment_type IN (
                'FULL_TIME',
                'PART_TIME',
                'CONTRACT',
                'FREELANCE',
                'INTERNSHIP',
                'TEMPORARY',
                'OTHER'
            )
        )
);

CREATE INDEX idx_experiences_display_order
    ON experiences (display_order);

CREATE INDEX idx_experiences_status_display_order
    ON experiences (status, display_order);

CREATE TABLE experience_technologies (
    experience_id UUID NOT NULL,
    technology VARCHAR(100) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT pk_experience_technologies
        PRIMARY KEY (experience_id, technology),

    CONSTRAINT fk_experience_technologies_experience
        FOREIGN KEY (experience_id)
        REFERENCES experiences (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_experience_technologies_order
    ON experience_technologies (experience_id, display_order);