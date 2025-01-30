CREATE TABLE files (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    Type VARCHAR(50) NOT NULL,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded BOOLEAN NOT NULL DEFAULT FALSE,
    file_size BIGINT
);

CREATE INDEX idx_document_id ON files USING BTREE (document_id);
