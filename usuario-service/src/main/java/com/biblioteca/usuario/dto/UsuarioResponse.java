package com.biblioteca.usuario.dto;

import com.biblioteca.usuario.model.Usuario;

// Dados que saem na resposta (repare: SEM a senha)
public record UsuarioResponse(Long id, String nome, String email) {

	public static UsuarioResponse from(Usuario u) {
		return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail());
	}
}
