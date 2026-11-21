package com.biblioteca.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação contendo o token JWT")
public record TokenResponse(

    @Schema(
        description = "Token JWT utilizado no cabeçalho Authorization " +
                      "com o prefixo Bearer"
    )
    String token

) {}
