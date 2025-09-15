package br.com.alura.adopet.api.infra.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Indica que essa classe contém configurações do Spring
public class SwaggerConfig {

    // Nome do esquema de segurança (será usado no Swagger UI)
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // -------------------- Informações da API --------------------
                .info(new Info()
                        .title("API PetHub") // Título exibido na doc
                        .version("1.0") // Versão da API
                        .description("Documentação da API de adoção de pets") // Descrição
                )

                // -------------------- Segurança --------------------
                // Adiciona requisito de segurança global (JWT)
                .addSecurityItem(
                        new SecurityRequirement().addList(SECURITY_SCHEME_NAME)
                )

                // Define o esquema de segurança para o Swagger reconhecer o JWT
                .components(
                        new io.swagger.v3.oas.models.Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .name(SECURITY_SCHEME_NAME)     // Nome do esquema
                                                .type(SecurityScheme.Type.HTTP) // Tipo HTTP (para bearer tokens)
                                                .scheme("bearer")               // Esquema Bearer
                                                .bearerFormat("JWT")            // Formato do token
                                )
                );
    }
}
