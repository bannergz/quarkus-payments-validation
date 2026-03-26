-- V1: Create reference and transaction tables for ms-payments

CREATE TABLE IF NOT EXISTS transaction_type (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE INDEX IF NOT EXISTS idx_transaction_type_name ON transaction_type (name);

CREATE TABLE IF NOT EXISTS transaction_status (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE INDEX IF NOT EXISTS idx_transaction_status_name ON transaction_status (name);

CREATE TABLE IF NOT EXISTS transaction (
    id                    SERIAL PRIMARY KEY,
    external_id           UUID        NOT NULL UNIQUE,
    transaction_type_id   INTEGER     NOT NULL REFERENCES transaction_type (id),
    transaction_status_id INTEGER     NOT NULL REFERENCES transaction_status (id),
    account_debit_id      UUID        NOT NULL,
    account_credit_id     UUID        NOT NULL,
    value                 NUMERIC(19, 4) NOT NULL,
    created_at            TIMESTAMPTZ NOT NULL,
    updated_at            TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_transaction_external_id ON transaction (external_id);

-- Seed reference data
INSERT INTO transaction_type (name) VALUES ('DEBIT')    ON CONFLICT (name) DO NOTHING;
INSERT INTO transaction_type (name) VALUES ('CREDIT')   ON CONFLICT (name) DO NOTHING;
INSERT INTO transaction_type (name) VALUES ('TRANSFER') ON CONFLICT (name) DO NOTHING;

INSERT INTO transaction_status (name) VALUES ('PENDING')  ON CONFLICT (name) DO NOTHING;
INSERT INTO transaction_status (name) VALUES ('APPROVED') ON CONFLICT (name) DO NOTHING;
INSERT INTO transaction_status (name) VALUES ('REJECTED') ON CONFLICT (name) DO NOTHING;
