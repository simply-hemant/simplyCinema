package com.simply.Cinema.config;

import com.simply.Cinema.security.jwt.JwtTokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.sessionManagement(management -> management
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )).authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**", "/api/otp/**").permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .requestMatchers("/theatre-owner/**").hasAnyRole("THEATRE_OWNER", "ADMIN")
                        .requestMatchers("/counter-staff/**").hasAnyRole("THEATRE_OWNER", "ADMIN", "COUNTER_STAFF")
                        .requestMatchers("/customer/**").hasAnyRole("THEATRE_OWNER", "ADMIN", "COUNTER_STAFF", "CUSTOMER")

                        // All other /api/ requires authentication

                        .requestMatchers("/api/**").authenticated() // Should come AFTER specific role matchers
                        .anyRequest().permitAll() // Allow everything else
                ).addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                        .formLogin(AbstractHttpConfigurer::disable) // 🔥 Disable default form login
                        .httpBasic(AbstractHttpConfigurer::disable); // 🔥 Disable HTTP Basic Auth

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration cfg = new CorsConfiguration();

        cfg.setAllowedOrigins(Collections.singletonList("*"));
        cfg.setAllowedMethods(Collections.singletonList("*"));
        cfg.setAllowedHeaders(Collections.singletonList("*"));
        cfg.setAllowCredentials(true);
        cfg.setExposedHeaders(Collections.singletonList("Authorization"));
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg); // Apply to all endpoints

        return source;

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


}
