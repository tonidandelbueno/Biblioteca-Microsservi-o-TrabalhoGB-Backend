package com.biblioteca.livro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.livro.model.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long> {

	List<Livro> findByTituloContainingIgnoreCase(String titulo);
}
