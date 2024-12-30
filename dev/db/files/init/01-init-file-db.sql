CREATE TABLE files (
    Id UUID PRIMARY KEY,
    DocumentId UUID NOT NULL,
    Path TEXT NOT NULL,
    Type VARCHAR(50) NOT NULL,
    CreatedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_document FOREIGN KEY (DocumentId) REFERENCES Document(Id) ON DELETE CASCADE
);

CREATE INDEX idx_document_id ON files USING BTREE (DocumentId);
