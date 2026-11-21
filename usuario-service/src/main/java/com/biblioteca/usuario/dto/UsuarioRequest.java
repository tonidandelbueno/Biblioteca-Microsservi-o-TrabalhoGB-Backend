package com.biblioteca.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para cadastro ou atualização de um usuário")
public record UsuarioRequest(

    @Schema(
        description = "Nome do usuário",
        example = "João Silva",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String nome,

    @Schema(
        description = "E-mail utilizado para identificação e login",
        example = "joao@exemplo.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String email,

    @Schema(
        description = "Senha do usuário, armazenada como hash BCrypt",
        format = "password",
        accessMode = Schema.AccessMode.WRITE_ONLY,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String senha

) {}
