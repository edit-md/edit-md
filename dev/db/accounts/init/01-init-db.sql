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
    user_id   UUID REFERENCES users (id),
    PRIMARY KEY (provider, remote_id),
    UNIQUE (provider, user_id)
);