package com.biblioteca.usuario.dto;

// Dados que chegam no POST/PUT
public record UsuarioRequest(String nome, String email, String senha) {
}
