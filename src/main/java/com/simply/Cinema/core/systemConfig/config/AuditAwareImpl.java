package com.simply.Cinema.core.systemConfig.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AuditAwareImpl {

    @Bean
    public AuditorAware<String> auditorAware(){
        return ()-> {
            return Optional.of("system_user");
        };
    }


//
//    @Bean
//    public AuditorAware<String> auditorAware() {
//        return new AuditorAware<String>() {
//            @Override
//            public Optional<String> getCurrentAuditor() {
//                return Optional.of("system_user");
//            }
//        };
//    }
}
