package com.biblioteca.emprestimo.dto;

public record LivroDto(Long id, String titulo, String autor, String isbn, boolean disponivel) {
}
