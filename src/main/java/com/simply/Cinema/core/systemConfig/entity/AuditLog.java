package com.simply.Cinema.core.systemConfig.entity;

import com.simply.Cinema.core.systemConfig.Enums.AuditAction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tableName;

    @Enumerated(EnumType.STRING)
    private AuditAction action; // CREATE, UPDATE, DELETE

    @Lob
    private String oldValue;

    @Lob
    private String newValue;

    private Long userId;

    private LocalDateTime createdAt;

    public AuditLog() {

    }
}
