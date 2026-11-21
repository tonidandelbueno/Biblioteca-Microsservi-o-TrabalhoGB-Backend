package com.biblioteca.emprestimo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "Dados necessários para registrar um empréstimo"
)
public record EmprestimoRequest(

    @Schema(
        description = "Identificador do usuário que realizará o empréstimo",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long usuarioId,

    @Schema(
        description = "Identificador do livro que será emprestado",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long livroId

) {}
