CREATE TABLE accounts (
                          id              UUID         NOT NULL DEFAULT gen_random_uuid(),
                          tenant_id       UUID         NOT NULL,
                          customer_id     UUID         NOT NULL,
                          account_number  VARCHAR(20)  NOT NULL,
                          type            VARCHAR(20)  NOT NULL,
                          status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
                          balance         NUMERIC(19,4) NOT NULL DEFAULT 0,
                          currency        VARCHAR(3)   NOT NULL DEFAULT 'INR',
                          version         BIGINT       NOT NULL DEFAULT 0,
                          created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                          updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

                          CONSTRAINT pk_accounts PRIMARY KEY (id),
                          CONSTRAINT fk_accounts_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
                          CONSTRAINT uq_accounts_tenant_number UNIQUE (tenant_id, account_number),
                          CONSTRAINT ck_accounts_type     CHECK (type   IN ('SAVINGS','CURRENT')),
                          CONSTRAINT ck_accounts_status   CHECK (status IN ('ACTIVE','FROZEN','CLOSED')),
                          CONSTRAINT ck_accounts_balance  CHECK (balance >= 0)
);

CREATE INDEX idx_accounts_tenant          ON accounts (tenant_id);
CREATE INDEX idx_accounts_tenant_customer ON accounts (tenant_id, customer_id);
