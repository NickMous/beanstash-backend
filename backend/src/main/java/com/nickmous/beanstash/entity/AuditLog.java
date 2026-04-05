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
@SequenceGenerator(name = "audit_log_seq", sequenceName = "audit_log.audit_log_seq")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE, generator = "audit_log_seq")
    private Long id;
    private String action;
    private Instant loggedAt;

    @ManyToOne(optional = false)
    private User actor;
    private String tableName;
    private String recordId;
    private String details;
    private Long version;
}
