package com.biblioteca.emprestimo.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Biblioteca - API de Empréstimos",
        version = "2.0",
        description = "Microsserviço responsável pelo gerenciamento " +
                      "de empréstimos e devoluções de livros, incluindo " +
                      "o registro de empréstimos, controle de prazos, " +
                      "histórico de operações e integração com os " +
                      "serviços de usuários e livros."
    )
)
public class OpenApiConfig {

}
