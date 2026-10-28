CREATE TABLE projects (
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(200) NOT NULL UNIQUE,
    summary VARCHAR(500) NOT NULL,
    content_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_projects_status
        CHECK (status IN ('DRAFT', 'PUBLISHED')),

    CONSTRAINT chk_projects_content_json_object
        CHECK (jsonb_typeof(content_json) = 'object')
);

CREATE INDEX idx_projects_status_order
    ON projects (status, display_order);

CREATE INDEX idx_projects_created_at
    ON projects (created_at DESC);