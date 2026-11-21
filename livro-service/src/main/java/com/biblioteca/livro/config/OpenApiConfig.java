package com.biblioteca.livro.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Biblioteca - API de Livros",
        version = "2.0",
        description = "Microsserviço responsável pelo gerenciamento " +
                      "do acervo da biblioteca, incluindo cadastro, " +
                      "consulta, atualização, exclusão e controle " +
                      "de disponibilidade dos livros."
    )
)
public class OpenApiConfig {

}
