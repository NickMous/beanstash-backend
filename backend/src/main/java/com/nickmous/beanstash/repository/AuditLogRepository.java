package com.nickmous.beanstash.repository;

import com.nickmous.beanstash.entity.AuditLog;
import org.springframework.data.repository.CrudRepository;

public interface AuditLogRepository extends CrudRepository<AuditLog, Long> {
}
