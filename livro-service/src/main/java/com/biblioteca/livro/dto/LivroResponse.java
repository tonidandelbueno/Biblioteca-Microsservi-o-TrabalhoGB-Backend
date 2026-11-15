package com.biblioteca.livro.dto;

import com.biblioteca.livro.model.Livro;

public record LivroResponse(Long id, String titulo, String autor, String isbn, boolean disponivel) {

	public static LivroResponse from(Livro l) {
		return new LivroResponse(l.getId(), l.getTitulo(), l.getAutor(), l.getIsbn(), l.isDisponivel());
	}
}
