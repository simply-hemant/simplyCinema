package com.simply.Cinema.core.systemConfig.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef =  "auditor-Aware")
public class JpaAuditConfig {
}
