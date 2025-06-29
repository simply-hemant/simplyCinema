package com.simply.Cinema.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.simply.Cinema.security.jwt.JwtTokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class AppConfig {

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http.sessionManagement(management -> management
//                        .sessionCreationPolicy(
//                                SessionCreationPolicy.STATELESS
//                        )).authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/auth/**", "/api/otp/**").permitAll()
//
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/theatre-owner/**").hasAnyRole("THEATRE_OWNER", "ADMIN")
//                        .requestMatchers("/counter-staff/**").hasAnyRole("THEATRE_OWNER", "ADMIN", "COUNTER_STAFF")
//                        .requestMatchers("/customer/**").hasAnyRole("THEATRE_OWNER", "ADMIN", "COUNTER_STAFF", "CUSTOMER")
//                        // All other /api/ requires authentication
//
//                        .requestMatchers("/api/**").authenticated() // Should come AFTER specific role matchers
//
//                        .anyRequest().permitAll() // Allow everything else
//                ).addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
//                .csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .formLogin(AbstractHttpConfigurer::disable) // 🔥 Disable default form login
//                .httpBasic(AbstractHttpConfigurer::disable); // 🔥 Disable HTTP Basic Auth
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        // 🔓 Public endpoints (no login required)
                        .requestMatchers(
                                "/api/auth/**",                  // login, register, otp
                                "/api/movies/**",                // movie listing & details
                                "/api/theatres/**",              // theatre listing & details
                                "/api/cities/**",                // cities and location
                                "/api/shows/**",                 // show availability
                                "/api/search/**",                // search movies, theatres
                                "/api/content/**",               // recommendations, trending
                                "/api/reviews/**",               // read reviews
                                "/api/bookings/guest",           // optional: guest booking
                                "/swagger-ui/**",                // Swagger (optional)
                                "/v3/api-docs/**"                // OpenAPI docs (optional)
                        ).permitAll()

                        // 🛡️ Role-based routes
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/theatre-owner/**").hasAnyRole("THEATRE_OWNER", "ADMIN")
                        .requestMatchers("/counter-staff/**").hasAnyRole("COUNTER_STAFF", "THEATRE_OWNER", "ADMIN")
                        .requestMatchers("/customer/**").hasAnyRole("CUSTOMER", "COUNTER_STAFF", "THEATRE_OWNER", "ADMIN")

                        // 🔐 All other `/api/**` endpoints require authentication
                        .requestMatchers("/api/**").authenticated()

                        // ✅ Allow everything else (like static files, home page)
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }


    //in production
    //Replace * with allowed frontend URLs:

    //cfg.setAllowedOrigins(List.of("https://bookmyshow-clone.com"));

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // ✅ Use patterns to allow wildcard with credentials (dev use)
        cfg.setAllowedOriginPatterns(Collections.singletonList("*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        cfg.setAllowCredentials(true);
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);

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


    public class JacksonConfig {
        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // Support for Java 8 DateTime, etc.
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            return mapper;
        }
    }


}