package com.biblioteca.livro.dto;

import io.swagger.v3.oas.annotations.media.Schema;

//dto atualizada para suport do swagger

@Schema(description = "Dados necessários para cadastrar ou atualizar um livro")
public record LivroRequest(

    @Schema(
        description = "Título do livro",
        example = "Programação 101",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String titulo,

    @Schema(
        description = "Nome do autor do livro",
        example = "Autor Exemplo",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String autor,

    @Schema(
        description = "Código ISBN do livro, quando informado",
        example = "9788575228771"
    )
    String isbn

) {}
