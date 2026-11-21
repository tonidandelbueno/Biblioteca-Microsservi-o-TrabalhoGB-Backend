
package com.biblioteca.usuario.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Biblioteca - API de Usuários",
        version = "2.0",
        description = "Microsserviço responsável pelo cadastro, " +
                      "consulta, atualização e exclusão de usuários, " +
                      "além da autenticação e validação de tokens JWT. " +
                      "A autenticação das requisições externas é " +
                      "realizada pelo API Gateway."
    )
)
public class OpenApiConfig {

}
