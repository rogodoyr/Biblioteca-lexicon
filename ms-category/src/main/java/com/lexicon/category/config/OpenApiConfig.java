package com.lexicon.category.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msCategoryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lexicon Category Service API")
                        .version("v1")
                        .description("OpenAPI documentation for the ms-category microservice"));
    }
}
