package com.biblioteca.emprestimo.dto;

import java.time.LocalDate;

import com.biblioteca.emprestimo.model.Emprestimo;
import com.biblioteca.emprestimo.model.StatusEmprestimo;

public record EmprestimoResponse(Long id, Long usuarioId, Long livroId, LocalDate dataEmprestimo,
		LocalDate dataPrevistaDevolucao, LocalDate dataDevolucao, StatusEmprestimo status) {

	public static EmprestimoResponse from(Emprestimo e) {
		return new EmprestimoResponse(e.getId(), e.getUsuarioId(), e.getLivroId(), e.getDataEmprestimo(),
				e.getDataPrevistaDevolucao(), e.getDataDevolucao(), e.getStatus());
	}
}
