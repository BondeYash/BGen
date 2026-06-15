-- V7: Money Transfer Engine + generalize ledger grouping
CREATE TABLE transfers (
                           id               UUID            NOT NULL,
                           tenant_id        UUID            NOT NULL,
                           from_account_id  UUID            NOT NULL,
                           to_account_id    UUID            NOT NULL,
                           amount           NUMERIC(19,4)   NOT NULL,
                           currency         VARCHAR(3)      NOT NULL,
                           idempotency_key  VARCHAR(80)     NOT NULL,
                           status           VARCHAR(20)     NOT NULL DEFAULT 'COMPLETED',
                           description      VARCHAR(255),
                           created_at       TIMESTAMPTZ     NOT NULL DEFAULT now(),

                           CONSTRAINT pk_transfers PRIMARY KEY (id),
                           CONSTRAINT fk_transfers_from FOREIGN KEY (from_account_id) REFERENCES accounts (id),
                           CONSTRAINT fk_transfers_to   FOREIGN KEY (to_account_id)   REFERENCES accounts (id),
                           CONSTRAINT uq_transfers_idem UNIQUE (tenant_id, idempotency_key),
                           CONSTRAINT ck_transfers_amount CHECK (amount > 0),
                           CONSTRAINT ck_transfers_status CHECK (status IN ('COMPLETED')),
                           CONSTRAINT ck_transfers_distinct CHECK (from_account_id <> to_account_id)
);

CREATE INDEX idx_transfers_tenant_created ON transfers (tenant_id, created_at DESC);

-- Generalize the ledger: transaction_id is now a polymorphic EVENT id
-- (a transaction id OR a transfer id), so transfers can post legs too.
ALTER TABLE ledger_entries DROP CONSTRAINT fk_ledger_txn;
