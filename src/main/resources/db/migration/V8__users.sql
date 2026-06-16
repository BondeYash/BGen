-- V8: authentication — users (login accounts) + their roles
CREATE TABLE users (
                       id            UUID         NOT NULL,
                       tenant_id     UUID         NOT NULL,
                       email         VARCHAR(255) NOT NULL,
                       password_hash VARCHAR(100) NOT NULL,
                       full_name     VARCHAR(255),
                       customer_id   UUID,
                       enabled       BOOLEAN      NOT NULL DEFAULT true,
                       created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

                       CONSTRAINT pk_users PRIMARY KEY (id),
                       CONSTRAINT uq_users_tenant_email UNIQUE (tenant_id, email),
                       CONSTRAINT fk_users_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE user_roles (
                            user_id UUID        NOT NULL,
                            role    VARCHAR(20) NOT NULL,

                            CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role),
                            CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                            CONSTRAINT ck_user_roles_role CHECK (role IN ('CUSTOMER','TELLER','MANAGER','ADMIN'))
);
