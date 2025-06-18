package com.simply.Cinema.service.systemConfig.impl;

import com.simply.Cinema.core.systemConfig.Enums.AuditAction;
import com.simply.Cinema.core.systemConfig.entity.AuditLog;
import com.simply.Cinema.core.systemConfig.repository.AuditLogRepo;
import com.simply.Cinema.service.systemConfig.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {


    private final AuditLogRepo auditLogRepo;

    @Override
    public void logEvent(String tableName, AuditAction action, String oldValue, String newValue, Long userId) {
        AuditLog auditLog = new AuditLog();

        auditLog.setTableName(tableName);
        auditLog.setAction(action);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);
        auditLog.setUserId(userId);
        auditLog.setCreatedAt(LocalDateTime.now());

        auditLogRepo.save(auditLog);
    }

}
