package com.simply.Cinema.core.systemConfig.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorConfig implements AuditorAware<Integer> {

    public Optional<Integer> getCurrentAuditor(){
        return Optional.of(1);
    }

}