package com.biblioteca.emprestimo.dto;

import java.time.LocalDate;

import com.biblioteca.emprestimo.model.Emprestimo;
import com.biblioteca.emprestimo.model.StatusEmprestimo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "Informações de um empréstimo registrado na biblioteca"
)
public record EmprestimoResponse(

    @Schema(
        description = "Identificador único do empréstimo",
        example = "1"
    )
    Long id,

    @Schema(
        description = "Identificador do usuário responsável pelo empréstimo",
        example = "1"
    )
    Long usuarioId,

    @Schema(
        description = "Identificador do livro emprestado",
        example = "1"
    )
    Long livroId,

    @Schema(
        description = "Data em que o empréstimo foi realizado",
        example = "2026-11-20"
    )
    LocalDate dataEmprestimo,

    @Schema(
        description = "Data prevista para devolução do livro",
        example = "2026-12-04"
    )
    LocalDate dataPrevistaDevolucao,

    @Schema(
        description = "Data efetiva da devolução. "
                    + "Permanece nula enquanto o empréstimo estiver ativo"
    )
    LocalDate dataDevolucao,

    @Schema(
        description = "Situação atual do empréstimo",
        example = "ATIVO",
        allowableValues = {"ATIVO", "DEVOLVIDO"}
    )
    StatusEmprestimo status

) {

    public static EmprestimoResponse from(Emprestimo e) {
        return new EmprestimoResponse(
            e.getId(),
            e.getUsuarioId(),
            e.getLivroId(),
            e.getDataEmprestimo(),
            e.getDataPrevistaDevolucao(),
            e.getDataDevolucao(),
            e.getStatus()
        );
    }
}
