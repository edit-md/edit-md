CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TYPE visibility_enum AS ENUM ('PRIVATE', 'PUBLIC');
CREATE TYPE permission_enum AS ENUM ('READ', 'WRITE');

CREATE TABLE IF NOT EXISTS document (
    id         UUID NOT NULL PRIMARY KEY,
    created    TIMESTAMP(6),
    owner      UUID NOT NULL,
    title      VARCHAR(255),
    visibility visibility_enum NOT NULL
);

CREATE TABLE IF NOT EXISTS document_data (
    id            UUID NOT NULL PRIMARY KEY,
    content       VARCHAR,
    last_modified TIMESTAMP(6),
    CONSTRAINT fk_document FOREIGN KEY (id)
        REFERENCES document (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS document_users (
    document_id UUID NOT NULL,
    user_id     UUID NOT NULL,
    permission  permission_enum NOT NULL,
    PRIMARY KEY (document_id, user_id),
    CONSTRAINT fk_document_user FOREIGN KEY (document_id)
        REFERENCES document (id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_document_owner ON document(owner);
CREATE INDEX IF NOT EXISTS idx_document_users_user_id ON document_users(user_id);
CREATE INDEX IF NOT EXISTS idx_documents_title_trgm ON document USING GIN (title gin_trgm_ops);