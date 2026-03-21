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
    record_id  TEXT,
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

INSERT INTO "user" (id, username, first_name, last_name, email, created_at)
VALUES ('00000000-0000-0000-0000-000000000000', 'system', 'System', 'User', 'system@beanstash.com', NOW());

CREATE OR REPLACE FUNCTION audit_log_trigger()
    RETURNS TRIGGER AS
$$
DECLARE
    v_action VARCHAR(255);
    v_user_id UUID;
    v_version BIGINT;
BEGIN
    IF TG_OP = 'INSERT' THEN
        v_action := 'INSERT';
    ELSIF TG_OP = 'UPDATE' THEN
        v_action := 'UPDATE';
    ELSIF TG_OP = 'DELETE' THEN
        v_action := 'DELETE';
    END IF;

    -- System user UUID (you can seed this in the user table or just use it as a convention)
    v_user_id = COALESCE(
            NULLIF(current_setting('app.current_user_id', true), '')::UUID,
            '00000000-0000-0000-0000-000000000000'::UUID
    );

    SELECT COALESCE(MAX(version), 0) + 1 INTO v_version
    FROM audit_log.audit_log
    WHERE table_name = TG_TABLE_NAME AND record_id = COALESCE((row_to_json(NEW) ->> TG_ARGV[0])::TEXT, (row_to_json(OLD) ->> TG_ARGV[0])::TEXT);

    INSERT INTO audit_log.audit_log (id, action, logged_at, actor_id, table_name, record_id, details, version)
    VALUES (
        nextval('audit_log.audit_log_seq'),
        v_action,
        NOW(),
        v_user_id,
        TG_TABLE_NAME,
        COALESCE((row_to_json(NEW) ->> TG_ARGV[0])::TEXT, (row_to_json(OLD) ->> TG_ARGV[0])::TEXT),
        row_to_json(NEW)::TEXT,
        v_version
   );

    RETURN NEW;
end;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE TRIGGER user_audit_log_trigger
    AFTER INSERT OR UPDATE OR DELETE ON "user"
    FOR EACH ROW
EXECUTE FUNCTION audit_log_trigger('id');
