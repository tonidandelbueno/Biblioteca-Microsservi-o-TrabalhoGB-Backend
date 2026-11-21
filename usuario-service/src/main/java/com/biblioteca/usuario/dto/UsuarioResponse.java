package com.biblioteca.usuario.dto;

import com.biblioteca.usuario.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informações públicas de um usuário cadastrado")
public record UsuarioResponse(

    @Schema(
        description = "Identificador único do usuário",
        example = "1"
    )
    Long id,

    @Schema(
        description = "Nome do usuário",
        example = "João Silva"
    )
    String nome,

    @Schema(
        description = "E-mail cadastrado",
        example = "joao@exemplo.com"
    )
    String email

) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(
            u.getId(),
            u.getNome(),
            u.getEmail()
        );
    }
}
