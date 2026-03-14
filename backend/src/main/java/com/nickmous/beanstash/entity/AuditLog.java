package com.nickmous.beanstash.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Data;

@Entity
@Data
@Table(name = "audit_log", schema = "audit_log")
@SequenceGenerator(sequenceName = "audit_log.audit_log_seq")
public class AuditLog {

    @Id
    @GeneratedValue
    private Long id;
    private String action;
    private Instant loggedAt;

    @ManyToOne(optional = false)
    private User actor;
    private String tableName;
    private Long recordId;
    private String details;
    private Long version;
}
