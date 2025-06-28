package com.simply.Cinema.core.systemConfig.entity;

import com.simply.Cinema.core.systemConfig.Enums.AuditAction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tableName;

    @Enumerated(EnumType.STRING)
    private AuditAction action; // CREATE, UPDATE, DELETE

//    @Lob
//    @Column(columnDefinition = "TEXT")
//    private String oldValue;
//
//    @Lob
//    @Column(columnDefinition = "TEXT")
//    private String newValue;

    private Long userId;

    private LocalDateTime createdAt;

}
