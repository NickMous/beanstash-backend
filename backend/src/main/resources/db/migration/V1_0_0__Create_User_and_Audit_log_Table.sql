CREATE SCHEMA IF NOT EXISTS audit_log;
GRANT USAGE ON SCHEMA audit_log TO ${app_user};
ALTER DEFAULT PRIVILEGES IN SCHEMA audit_log GRANT SELECT ON TABLES TO ${app_user};

CREATE SEQUENCE IF NOT EXISTS audit_log.audit_log_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE audit_log.audit_log
(
    id         BIGINT NOT NULL,
    action     VARCHAR(255),
    logged_at  TIMESTAMP WITHOUT TIME ZONE,
    actor_id   UUID   NOT NULL,
    table_name VARCHAR(255),
    record_id  BIGINT,
    details    VARCHAR(255),
    version    BIGINT,
    CONSTRAINT pk_audit_log PRIMARY KEY (id)
);

CREATE TABLE "user"
(
    id         UUID NOT NULL,
    username   VARCHAR(255),
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    email      VARCHAR(255),
    password   VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_user PRIMARY KEY (id)
);
