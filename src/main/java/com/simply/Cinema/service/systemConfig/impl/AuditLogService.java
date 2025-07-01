package com.simply.Cinema.service.systemConfig.impl;

import com.simply.Cinema.core.systemConfig.Enums.AuditAction;
import com.simply.Cinema.core.systemConfig.entity.AuditLog;
import com.simply.Cinema.core.systemConfig.repository.AuditLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepo auditLogRepo;

    public void logEvent(String tableName, AuditAction action, Long entityId, Long userId) {
        AuditLog log = AuditLog.builder()
                .tableName(tableName)
                .action(action)
                .entityId(entityId)
                .userId(userId) // ✅ Use the passed userId directly
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepo.save(log);
        System.out.println("User ID saved in Audit Log: " + userId);
    }

}
