package com.biblioteca.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciais utilizadas para autenticação")
public record LoginRequest(

    @Schema(
        description = "E-mail do usuário cadastrado",
        example = "joao@exemplo.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String email,

    @Schema(
        description = "Senha utilizada para autenticação",
        format = "password",
        accessMode = Schema.AccessMode.WRITE_ONLY,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String senha

) {}
