
package com.biblioteca.livro.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.livro.dto.LivroRequest;
import com.biblioteca.livro.dto.LivroResponse;
import com.biblioteca.livro.service.LivroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/livro")
@Tag(
    name = "Livros",
    description = "Operações de gerenciamento do acervo da biblioteca"
)
public class LivroController {

    private final LivroService service;

    public LivroController(LivroService service) {
        this.service = service;
    }

    @Operation(
        summary = "Cadastrar livro",
        description = "Cadastra um novo livro no acervo. " +
                      "O livro é criado inicialmente disponível."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Livro cadastrado com sucesso",
            content = @Content(
                schema = @Schema(implementation = LivroResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados obrigatórios inválidos"
        )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LivroResponse cadastrar(@RequestBody LivroRequest req) {
        return service.cadastrar(req);
    }

    @Operation(
        summary = "Listar livros",
        description = "Retorna os livros cadastrados. " +
                      "Permite filtrar pelo título."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada com sucesso",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = LivroResponse.class)
            )
        )
    )
    @GetMapping
    public List<LivroResponse> listar(
            @Parameter(
                description = "Título ou parte do título do livro",
                example = "Java"
            )
            @RequestParam(required = false) String titulo) {

        return service.listar(titulo);
    }

    @Operation(
        summary = "Buscar livro por ID",
        description = "Consulta as informações de um livro cadastrado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Livro encontrado",
            content = @Content(
                schema = @Schema(implementation = LivroResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Livro não encontrado"
        )
    })
    @GetMapping("/{id}")
    public LivroResponse buscar(
            @Parameter(description = "Identificador do livro", example = "1")
            @PathVariable Long id) {

        return service.buscar(id);
    }

    @Operation(
        summary = "Atualizar dados do livro",
        description = "Atualiza título, autor e ISBN de um livro. " +
                      "Não altera sua disponibilidade ou reserva."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Livro atualizado com sucesso",
            content = @Content(
                schema = @Schema(implementation = LivroResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados obrigatórios inválidos"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Livro não encontrado"
        )
    })
    @PutMapping("/{id}")
    public LivroResponse atualizar(
            @Parameter(description = "Identificador do livro", example = "1")
            @PathVariable Long id,
            @RequestBody LivroRequest req) {

        return service.atualizar(id, req);
    }

    @Operation(
        summary = "Excluir livro",
        description = "Exclui um livro do acervo somente quando " +
                      "não possui uma reserva ativa."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Livro excluído com sucesso",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Livro não encontrado"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Livro está reservado e não pode ser excluído"
        )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(
            @Parameter(description = "Identificador do livro", example = "1")
            @PathVariable Long id) {

        service.excluir(id);
    }

    @Operation(
        summary = "Reservar livro",
        description = "Operação interna utilizada pelo serviço de " +
                      "empréstimos. Reserva atomicamente um livro " +
                      "disponível e associa uma chave UUID à reserva."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Livro reservado com sucesso",
            content = @Content(
                schema = @Schema(implementation = LivroResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Chave de reserva ausente ou inválida"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Livro não encontrado"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Livro já está indisponível"
        )
    })
    @PutMapping("/{id}/reservar")
    public LivroResponse reservar(
            @Parameter(description = "Identificador do livro", example = "1")
            @PathVariable Long id,

            @Parameter(
                description = "UUID que identifica a reserva",
                required = true
            )
            @RequestParam UUID chave) {

        return service.reservar(id, chave);
    }

    @Operation(
        summary = "Liberar reserva de livro",
        description = "Operação interna utilizada pelo serviço de " +
                      "empréstimos. Libera o livro somente quando " +
                      "a chave UUID corresponde à reserva registrada."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Reserva liberada com sucesso",
            content = @Content(
                schema = @Schema(implementation = LivroResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Chave de reserva ausente ou inválida"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Livro não encontrado"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "A chave não corresponde à reserva do livro"
        )
    })
    @PutMapping("/{id}/liberar")
    public LivroResponse liberar(
            @Parameter(description = "Identificador do livro", example = "1")
            @PathVariable Long id,

            @Parameter(
                description = "UUID associado à reserva que será liberada",
                required = true
            )
            @RequestParam UUID chave) {

        return service.liberar(id, chave);
    }
}

