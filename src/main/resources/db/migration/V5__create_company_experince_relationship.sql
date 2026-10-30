CREATE TABLE companies (
    id UUID PRIMARY KEY,
    name VARCHAR(180) NOT NULL,
    slug VARCHAR(180) NOT NULL UNIQUE,
    description TEXT,
    website VARCHAR(255),
    logo_url VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_companies_slug ON companies (slug);

-- Backfill companies from existing experiences (if any exist)
INSERT INTO companies (id, name, slug, created_at, updated_at)
SELECT
    gen_random_uuid(),
    e.company,
    lower(regexp_replace(regexp_replace(trim(e.company), '[^a-zA-Z0-9]+', '-', 'g'), '^-|-$', '', 'g')),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM (SELECT DISTINCT company FROM experiences WHERE company IS NOT NULL AND trim(company) <> '') e
ON CONFLICT (slug) DO NOTHING;

-- Add company_id column to experiences
ALTER TABLE experiences
    ADD COLUMN company_id UUID;

-- Associate existing experiences with their companies by matching name
UPDATE experiences e
SET company_id = c.id
FROM companies c
WHERE trim(lower(c.name)) = trim(lower(e.company));

-- In case there are experiences with no matching company, create a fallback company and link them
DO $$
DECLARE
    fallback_company_id UUID;
BEGIN
    IF EXISTS (SELECT 1 FROM experiences WHERE company_id IS NULL) THEN
        fallback_company_id := gen_random_uuid();
        INSERT INTO companies (id, name, slug, created_at, updated_at)
        VALUES (fallback_company_id, 'Outra Empresa', 'outra-empresa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ON CONFLICT (slug) DO UPDATE SET name = EXCLUDED.name
        RETURNING id INTO fallback_company_id;

        UPDATE experiences
        SET company_id = fallback_company_id
        WHERE company_id IS NULL;
    END IF;
END $$;

-- Enforce NOT NULL and foreign key constraint on company_id
ALTER TABLE experiences
    ALTER COLUMN company_id SET NOT NULL;

ALTER TABLE experiences
    ADD CONSTRAINT fk_experiences_company
    FOREIGN KEY (company_id)
    REFERENCES companies (id)
    ON DELETE CASCADE;

CREATE INDEX idx_experiences_company_id
    ON experiences (company_id);

-- Drop the redundant textual company column
ALTER TABLE experiences
    DROP COLUMN company;
