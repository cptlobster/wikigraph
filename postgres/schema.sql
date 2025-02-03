-- all pages are stored in a single table
CREATE TABLE IF NOT EXISTS pages (
    id INT PRIMARY KEY,
    title VARCHAR(120),
    namespace INT
)

-- links between pages are stored in a separate table (many-to-many)
CREATE TABLE IF NOT EXISTS links (
    l_from INT REFERENCES pages (id),
    l_to INT REFERENCES pages (id)
)

-- index pages by ID
CREATE INDEX idx_id ON pages USING HASH (id)
-- create indexes for searching links from either direction
CREATE INDEX idx_from ON links USING HASH (l_from)
CREATE INDEX idx_to ON links USING HASH (l_to)