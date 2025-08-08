package com.simply.Cinema.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class swaggerConfig {

    @Bean
    public OpenAPI myCustomConfig(){
        return new OpenAPI()
                .info(
                        new Info().title("SimplyCinema APIs")
                                .description("By Hemant")
                )
                // link : http://localhost:8080/swagger-ui/index.html#/   (local)
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080").description("local"),
                        new Server().url("http://localhost:8082").description("live")
                ));
    }

}
