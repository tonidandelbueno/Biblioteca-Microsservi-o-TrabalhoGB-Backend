package com.biblioteca.emprestimo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.emprestimo.model.Emprestimo;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

	List<Emprestimo> findByUsuarioId(Long usuarioId);
}
