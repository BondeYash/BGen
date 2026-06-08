CREATE TABLE customers (
                           id               UUID         NOT NULL DEFAULT gen_random_uuid(),
                           tenant_id        UUID         NOT NULL,
                           customer_number  VARCHAR(20)  NOT NULL,
                           type             VARCHAR(20)  NOT NULL,
                           status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
                           full_name        VARCHAR(150) NOT NULL,
                           email            VARCHAR(255),
                           phone            VARCHAR(20),
                           date_of_birth    DATE,
                           registration_no  VARCHAR(50),
                           kyc_status       VARCHAR(20)  NOT NULL DEFAULT 'NOT_VERIFIED',
                           version          BIGINT       NOT NULL DEFAULT 0,
                           created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
                           updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),

                           CONSTRAINT pk_customers PRIMARY KEY (id),
                           CONSTRAINT uq_customers_tenant_number UNIQUE (tenant_id, customer_number),
                           CONSTRAINT uq_customers_tenant_email  UNIQUE (tenant_id, email),
                           CONSTRAINT ck_customers_type   CHECK (type IN ('INDIVIDUAL','CORPORATE')),
                           CONSTRAINT ck_customers_status CHECK (status IN ('PENDING','ACTIVE','SUSPENDED','CLOSED')),
                           CONSTRAINT ck_customers_kyc    CHECK (kyc_status IN ('NOT_VERIFIED','PENDING','VERIFIED','REJECTED'))
);

CREATE INDEX idx_customers_tenant         ON customers (tenant_id);
CREATE INDEX idx_customers_tenant_status  ON customers (tenant_id, status);
