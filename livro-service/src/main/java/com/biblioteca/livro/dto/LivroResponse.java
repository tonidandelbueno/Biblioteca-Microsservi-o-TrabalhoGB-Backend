package com.biblioteca.livro.dto;

import com.biblioteca.livro.model.Livro;
import io.swagger.v3.oas.annotations.media.Schema;

//implementação do swagger
@Schema(description = "Informações de um livro cadastrado na biblioteca")
public record LivroResponse(

    @Schema(
        description = "Identificador único do livro",
        example = "1"
    )
    Long id,

    @Schema(
        description = "Título do livro",
        example = "Programação Java"
    )
    String titulo,

    @Schema(
        description = "Nome do autor do livro",
        example = "Autor Exemplo"
    )
    String autor,

    @Schema(
        description = "Código ISBN do livro",
        example = "9788575228771"
    )
    String isbn,

    @Schema(
        description = "Indica se o livro está disponível para empréstimo",
        example = "true"
    )
    boolean disponivel

) {

    public static LivroResponse from(Livro l) {
        return new LivroResponse(
            l.getId(),
            l.getTitulo(),
            l.getAutor(),
            l.getIsbn(),
            l.isDisponivel()
        );
    }
}
