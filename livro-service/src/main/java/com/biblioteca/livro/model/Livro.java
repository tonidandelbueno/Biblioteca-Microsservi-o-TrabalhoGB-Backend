package com.biblioteca.livro.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String autor;
    private String isbn;

    // true = disponível; false = emprestado
    private boolean disponivel = true;

    // Identificador da reserva ativa
    private UUID chaveReserva;

    public Livro() {
    }
}
