package co.casino.audit.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI auditApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Audit Service API")
                        .description("Servicio de auditoría del sistema de casino")
                        .version("1.0"));
    }
}