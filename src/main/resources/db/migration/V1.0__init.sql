CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE card
(
    id         UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    alias      VARCHAR(100) NOT NULL,
    account_id UUID         NOT NULL,
    type       VARCHAR(20)  NOT NULL CHECK (type IN ('VIRTUAL', 'PHYSICAL')),
    pan        CHAR(16)     NOT NULL,
    cvv        CHAR(3)      NOT NULL,
    voided     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Enforce one card per type per account
CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_active_card_per_type ON card(account_id, type) WHERE voided = FALSE;

-- Index searchable fields
CREATE INDEX idx_card_alias ON card (alias);
CREATE INDEX idx_card_type ON card (type);
CREATE INDEX idx_card_pan ON card (pan);
