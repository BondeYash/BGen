-- V6: Ledger System (double-entry, immutable, append-only)
CREATE TABLE ledger_entries (
                                id              UUID            NOT NULL,
                                tenant_id       UUID            NOT NULL,
                                transaction_id  UUID            NOT NULL,
                                account_ref     VARCHAR(64)     NOT NULL,
                                direction       VARCHAR(6)      NOT NULL,
                                amount          NUMERIC(19,4)   NOT NULL,
                                currency        VARCHAR(3)      NOT NULL,
                                created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),

                                CONSTRAINT pk_ledger_entries PRIMARY KEY (id),
                                CONSTRAINT fk_ledger_txn FOREIGN KEY (transaction_id)
                                    REFERENCES transactions (id),
                                CONSTRAINT ck_ledger_direction CHECK (direction IN ('DEBIT', 'CREDIT')),
                                CONSTRAINT ck_ledger_amount_positive CHECK (amount > 0)
);

-- balance derivation + statement read (per account, time-ordered)
CREATE INDEX idx_ledger_tenant_account_created
    ON ledger_entries (tenant_id, account_ref, created_at);

-- fetch all legs of one event (for the zero-sum balance check)
CREATE INDEX idx_ledger_tenant_txn
    ON ledger_entries (tenant_id, transaction_id);
