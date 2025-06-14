package com.simply.Cinema.service.config;

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

    public void logEvent(String tableName, Long recordId, AuditAction action, String oldValue, String newValue, Long userId) {
        AuditLog auditLog = new AuditLog();

        auditLog.setTableName(tableName);
        auditLog.setRecordId(recordId);
        auditLog.setAction(action);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);
        auditLog.setUserId(userId);
        auditLog.setCreatedAt(LocalDateTime.now());

        auditLogRepo.save(auditLog);
    }

}
