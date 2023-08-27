CREATE TABLE IF NOT EXISTS customer (
    id UUID PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    roles TEXT[],
    hashed_password TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS username_index ON customer (username);

CREATE INDEX IF NOT EXISTS id_index ON customer (id);

CREATE TABLE IF NOT EXISTS password_reset (
    id UUID PRIMARY KEY,
    user_id TEXT NOT NULL,
    state TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS contact (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    type TEXT NOT NULL,
    status TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    chat_id TEXT,
    email   TEXT
);
