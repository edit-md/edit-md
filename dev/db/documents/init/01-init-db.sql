CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TYPE visibility_enum AS ENUM ('PRIVATE', 'PUBLIC');
CREATE TYPE permission_enum AS ENUM ('READ', 'WRITE');
CREATE TYPE change_type_enum AS ENUM ('INSERT', 'DELETE');

-- Document table
CREATE TABLE IF NOT EXISTS document (
    id         UUID NOT NULL PRIMARY KEY,
    created    TIMESTAMP(6),
    owner      UUID NOT NULL,
    title      VARCHAR(255),
    visibility visibility_enum NOT NULL
);

-- Document data table
CREATE TABLE IF NOT EXISTS document_data (
    id            UUID NOT NULL PRIMARY KEY,
    content       VARCHAR,
    revision      BIGINT NOT NULL,
    last_modified TIMESTAMP(6),
    CONSTRAINT fk_document FOREIGN KEY (id)
        REFERENCES document (id)
        ON DELETE CASCADE
);

-- Document users table
CREATE TABLE IF NOT EXISTS document_users (
    document_id UUID NOT NULL,
    user_id     UUID NOT NULL,
    permission  permission_enum NOT NULL,
    PRIMARY KEY (document_id, user_id),
    CONSTRAINT fk_document_user FOREIGN KEY (document_id)
        REFERENCES document (id)
        ON DELETE CASCADE
);

-- Document changes table
CREATE TABLE IF NOT EXISTS document_changes (
    document_id   UUID NOT NULL,
    revision      BIGINT NOT NULL,
    user_id       UUID NOT NULL,
    websocket_id  VARCHAR,
    index         BIGINT NOT NULL,
    change_type   change_type_enum NOT NULL,
    content       VARCHAR,
    length        BIGINT NOT NULL,
    created       TIMESTAMP(6),
    PRIMARY KEY (document_id, revision),
    CONSTRAINT fk_document FOREIGN KEY (document_id)
        REFERENCES document (id)
        ON DELETE CASCADE
);

-- Trigger for notifying on insert of document_changes
CREATE OR REPLACE FUNCTION notify_document_changes_insert()
RETURNS TRIGGER AS $$
DECLARE
notification_payload JSON;
BEGIN
    notification_payload = json_build_object(
        'id', json_build_object(
            'document_id', NEW.document_id,
            'revision', NEW.revision
        ),
        'user_id', NEW.user_id,
        'websocket_id', NEW.websocket_id,
        'index', NEW.index,
        'change_type', NEW.change_type,
        'content', NEW.content,
        'length', NEW.length,
        'created', NEW.created
    );

    PERFORM pg_notify('document_changes_channel', notification_payload::TEXT);
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger
CREATE TRIGGER trg_notify_document_changes_insert
    AFTER INSERT ON document_changes
    FOR EACH ROW
    EXECUTE FUNCTION notify_document_changes_insert();

CREATE INDEX IF NOT EXISTS idx_document_owner ON document(owner);
CREATE INDEX IF NOT EXISTS idx_document_users_user_id ON document_users(user_id);
CREATE INDEX IF NOT EXISTS idx_documents_title_trgm ON document USING GIN (title gin_trgm_ops);