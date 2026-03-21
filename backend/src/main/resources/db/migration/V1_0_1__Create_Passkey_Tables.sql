create table user_entities
(
    id           varchar(1000) not null,
    name         varchar(100)  not null,
    display_name varchar(200),
    primary key (id)
);

create table user_credentials
(
    credential_id                varchar(1000) not null,
    user_entity_user_id          varchar(1000) not null,
    public_key                   bytea         not null,
    signature_count              bigint,
    uv_initialized               boolean,
    backup_eligible              boolean       not null,
    authenticator_transports     varchar(1000),
    public_key_credential_type   varchar(100),
    backup_state                 boolean       not null,
    attestation_object           bytea,
    attestation_client_data_json bytea,
    created                      timestamp,
    last_used                    timestamp,
    label                        varchar(1000) not null,
    primary key (credential_id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON user_entities TO ${app_user};
GRANT SELECT, INSERT, UPDATE, DELETE ON user_credentials TO ${app_user};

CREATE TRIGGER user_entities_audit_log_trigger
    AFTER INSERT OR UPDATE OR DELETE ON user_entities
    FOR EACH ROW
EXECUTE FUNCTION audit_log_trigger('id');

CREATE TRIGGER user_credentials_audit_log_trigger
    AFTER INSERT OR UPDATE OR DELETE ON user_credentials
    FOR EACH ROW
EXECUTE FUNCTION audit_log_trigger('credential_id');

