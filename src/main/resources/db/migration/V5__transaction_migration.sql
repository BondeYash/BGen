CREATE TABLE transactions (
                              id               UUID          NOT NULL,
                              tenant_id        UUID          NOT NULL,
                              account_id       UUID          NOT NULL,
                              type             VARCHAR(20)   NOT NULL,
                              amount           NUMERIC(19,4) NOT NULL,
                              currency         VARCHAR(3)    NOT NULL,
                              balance_after    NUMERIC(19,4) NOT NULL,
                              idempotency_key  VARCHAR(80)   NOT NULL,
                              description      VARCHAR(255),
                              created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),

                              CONSTRAINT pk_transactions       PRIMARY KEY (id),
                              CONSTRAINT fk_txn_account        FOREIGN KEY (account_id) REFERENCES accounts(id),
                              CONSTRAINT uq_txn_idempotency    UNIQUE (tenant_id, idempotency_key),
                              CONSTRAINT chk_txn_amount_pos    CHECK (amount > 0),
                              CONSTRAINT chk_txn_type          CHECK (type IN ('DEPOSIT','WITHDRAWAL'))
);

CREATE INDEX idx_txn_tenant_account_created
    ON transactions (tenant_id, account_id, created_at DESC);
