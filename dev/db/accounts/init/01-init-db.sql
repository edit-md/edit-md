CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE users (
    id     UUID NOT NULL PRIMARY KEY,
    avatar VARCHAR(255),
    email  VARCHAR(255),
    name   VARCHAR(255),
    settings JSONB
);

CREATE TABLE connected_accounts (
    provider  VARCHAR(255) NOT NULL,
    remote_id VARCHAR(255) NOT NULL,
    user_id   UUID REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (provider, remote_id),
    UNIQUE (provider, user_id)
);

CREATE INDEX idx_users_name_trgm ON users USING GIN (name gin_trgm_ops);