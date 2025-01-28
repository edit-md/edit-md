CREATE TABLE files (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL,
    Path TEXT NOT NULL,
    Type VARCHAR(50) NOT NULL,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_document_id ON files USING BTREE (document_id);
