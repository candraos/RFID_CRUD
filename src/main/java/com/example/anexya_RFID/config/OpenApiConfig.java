package com.example.anexya_RFID.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI anexyaRfidOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Anexya RFID API")
                        .description("REST API for creating, retrieving, updating and deleting RFID tag reads.")
                        .version("v1")
                        .contact(new Contact().name("Anexya RFID Team")));
    }
}
