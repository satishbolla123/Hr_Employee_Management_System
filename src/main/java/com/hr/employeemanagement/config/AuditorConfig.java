package com.hr.employeemanagement.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.hr.employeemanagement.util.SecurityUtil;

@Configuration
@EnableJpaAuditing
public class AuditorConfig {

    // fills createdBy / lastModifiedBy with the logged in username
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(SecurityUtil.currentUsername());
    }
}
