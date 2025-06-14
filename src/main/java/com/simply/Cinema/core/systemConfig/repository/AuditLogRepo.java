package com.simply.Cinema.core.systemConfig.repository;

import com.simply.Cinema.core.systemConfig.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepo extends JpaRepository<AuditLog ,Long> {
}
