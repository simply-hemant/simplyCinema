package com.simply.Cinema.service.systemConfig;

import com.simply.Cinema.core.systemConfig.Enums.AuditAction;

public interface AuditLogService {

     void logEvent(String tableName, AuditAction action, String oldValue, String newValue, Long userId) ;

}
