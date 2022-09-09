package ru.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@OpenAPIDefinition(info = @Info(title = "NOTIFICATION API", version = "v1"))
public class OpenApi30Config {

    public static final String BEARER_AUTH = HttpHeaders.AUTHORIZATION;

}
