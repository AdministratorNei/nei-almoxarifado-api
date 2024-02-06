package com.ufrn.nei.almoxarifadoapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocsOpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Rest Api - Controle Almoxarifado")
                                .description("Api para o gerencimaneto do almoxarifado da instituição NEI")
                                .version("v1")
                                .license(new License().name("MIT").url("https://mit-license.org/"))
                );
    }
}
