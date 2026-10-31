-- 1. Add status column to companies with default 'DRAFT'
ALTER TABLE companies
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'DRAFT';

-- 2. Add check constraint to companies.status
ALTER TABLE companies
    ADD CONSTRAINT chk_companies_status
    CHECK (status IN ('DRAFT', 'PUBLISHED'));

-- 3. Migrate publication status: companies with published experiences become PUBLISHED
UPDATE companies
SET status = 'PUBLISHED'
WHERE id IN (
    SELECT DISTINCT company_id
    FROM experiences
    WHERE status = 'PUBLISHED'
);

-- 4. Create index for companies by status
CREATE INDEX idx_companies_status ON companies (status);

-- 5. Drop status index and check constraint from experiences
DROP INDEX IF EXISTS idx_experiences_status_display_order;

ALTER TABLE experiences
    DROP CONSTRAINT IF EXISTS chk_experiences_status;

-- 6. Drop status column from experiences
ALTER TABLE experiences
    DROP COLUMN status;

