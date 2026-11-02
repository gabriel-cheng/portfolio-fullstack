-- 1. Create technologies table
CREATE TABLE technologies (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_technologies_name_lower ON technologies (LOWER(name));

-- Seed default technologies
INSERT INTO technologies (id, name, slug, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Java', 'java', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Spring Boot', 'spring-boot', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'React', 'react', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'TypeScript', 'typescript', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'JavaScript', 'javascript', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'PostgreSQL', 'postgresql', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Docker', 'docker', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Node.js', 'nodejs', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Tailwind CSS', 'tailwind-css', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Next.js', 'nextjs', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Python', 'python', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Git', 'git', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'GitHub', 'github', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'REST APIs', 'rest-apis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'MySQL', 'mysql', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'MongoDB', 'mongodb', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Redis', 'redis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Linux', 'linux', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'AWS', 'aws', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Estruturas de Dados', 'estruturas-de-dados', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Populate technologies from experience_technologies
INSERT INTO technologies (id, name, slug, created_at, updated_at)
SELECT
    gen_random_uuid(),
    t.name,
    LOWER(REGEXP_REPLACE(REGEXP_REPLACE(t.name, '[^a-zA-Z0-9]+', '-', 'g'), '^-|-$', '', 'g')),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM (
    SELECT DISTINCT TRIM(technology) AS name
    FROM experience_technologies
    WHERE technology IS NOT NULL AND TRIM(technology) <> ''
) t
WHERE LOWER(t.name) NOT IN (SELECT LOWER(name) FROM technologies)
ON CONFLICT DO NOTHING;

-- Populate technologies from education_skills
INSERT INTO technologies (id, name, slug, created_at, updated_at)
SELECT
    gen_random_uuid(),
    s.name,
    LOWER(REGEXP_REPLACE(REGEXP_REPLACE(s.name, '[^a-zA-Z0-9]+', '-', 'g'), '^-|-$', '', 'g')),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM (
    SELECT DISTINCT TRIM(skill) AS name
    FROM education_skills
    WHERE skill IS NOT NULL AND TRIM(skill) <> ''
) s
WHERE LOWER(s.name) NOT IN (SELECT LOWER(name) FROM technologies)
ON CONFLICT DO NOTHING;

-- 2. Refactor projects table
ALTER TABLE projects ADD COLUMN number VARCHAR(10) NOT NULL DEFAULT '01';
ALTER TABLE projects ADD COLUMN category VARCHAR(100) NOT NULL DEFAULT 'Full-Stack';
ALTER TABLE projects ADD COLUMN publication_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT';
ALTER TABLE projects ADD COLUMN description TEXT NOT NULL DEFAULT '';
ALTER TABLE projects ADD COLUMN live_url VARCHAR(500);
ALTER TABLE projects ADD COLUMN github_url VARCHAR(500);
ALTER TABLE projects ADD COLUMN image VARCHAR(500) DEFAULT '';

-- Drop old check constraints and indexes BEFORE altering column data
ALTER TABLE projects DROP CONSTRAINT IF EXISTS chk_projects_status;
ALTER TABLE projects DROP CONSTRAINT IF EXISTS chk_projects_content_json_object;
DROP INDEX IF EXISTS idx_projects_status_order;

-- Migrate data
UPDATE projects SET description = summary WHERE summary IS NOT NULL AND summary <> '';
UPDATE projects SET publication_status = status WHERE status IN ('DRAFT', 'PUBLISHED');
UPDATE projects SET status = 'Em desenvolvimento';

-- Drop old columns
ALTER TABLE projects DROP COLUMN IF EXISTS summary;
ALTER TABLE projects DROP COLUMN IF EXISTS content_json;

-- Add new constraints and indexes
ALTER TABLE projects ADD CONSTRAINT chk_projects_publication_status CHECK (publication_status IN ('DRAFT', 'PUBLISHED'));
CREATE INDEX idx_projects_publication_order ON projects (publication_status, display_order);
CREATE INDEX idx_projects_number ON projects (number);

-- 3. Create project_highlights table
CREATE TABLE project_highlights (
    project_id UUID NOT NULL,
    highlight TEXT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT pk_project_highlights
        PRIMARY KEY (project_id, display_order),

    CONSTRAINT fk_project_highlights_project
        FOREIGN KEY (project_id)
        REFERENCES projects (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_project_highlights_order
    ON project_highlights (project_id, display_order);

-- 4. Create project_technologies table
CREATE TABLE project_technologies (
    project_id UUID NOT NULL,
    technology VARCHAR(100) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT pk_project_technologies
        PRIMARY KEY (project_id, technology),

    CONSTRAINT fk_project_technologies_project
        FOREIGN KEY (project_id)
        REFERENCES projects (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_project_technologies_order
    ON project_technologies (project_id, display_order);
