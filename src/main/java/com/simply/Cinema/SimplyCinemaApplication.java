package com.simply.Cinema;

import com.simply.Cinema.core.systemConfig.config.AuditorConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class SimplyCinemaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimplyCinemaApplication.class, args);
	}


}
