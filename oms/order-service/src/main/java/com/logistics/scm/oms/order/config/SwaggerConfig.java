package com.logistics.scm.oms.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 설정
 * 
 * 접근 경로:
 * - Swagger UI: http://localhost:8081/swagger-ui/index.html
 * - API Docs: http://localhost:8081/v3/api-docs
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:order-service}")
    private String applicationName;

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .description("SCM 플랫폼의 주문 관리 서비스 API 문서입니다.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("SCM Team")
                                .email("scm@logistics.com")
                                .url("https://logistics.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Order Service (Direct)"),
                        new Server()
                                .url("http://localhost:8080/order-service")
                                .description("API Gateway (Routed)")
                ));
    }
}
