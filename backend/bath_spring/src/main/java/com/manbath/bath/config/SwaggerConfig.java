package com.manbath.bath.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition

public class SwaggerConfig {

    public OpenAPI baseOpenAPI() {
        return new OpenAPI().info(new Info().title("API 테스트").version("0.8").description("API 테스트 사이트"));
    }
}
