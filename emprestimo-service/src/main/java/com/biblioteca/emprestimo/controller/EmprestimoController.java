package com.biblioteca.emprestimo.controller;

import java.util.List;

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

import com.biblioteca.emprestimo.dto.EmprestimoRequest;
import com.biblioteca.emprestimo.dto.EmprestimoResponse;
import com.biblioteca.emprestimo.service.EmprestimoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/emprestimo")
@Tag(
    name = "Empréstimos",
    description = "Operações de empréstimo, devolução e histórico da biblioteca"
)
public class EmprestimoController {

    private final EmprestimoService service;

    public EmprestimoController(EmprestimoService service) {
        this.service = service;
    }

    @Operation(
        summary = "Registrar empréstimo",
        description = "Registra um empréstimo para um usuário existente. " +
                      "Verifica o usuário e reserva atomicamente o livro, " +
                      "impedindo empréstimos simultâneos do mesmo exemplar. " +
                      "O prazo de devolução é definido pela configuração do serviço."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Empréstimo registrado com sucesso",
            content = @Content(
                schema = @Schema(implementation = EmprestimoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Usuário ou livro não informado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário ou livro não encontrado"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Livro indisponível para empréstimo"
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Serviço de usuários ou livros indisponível"
        )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmprestimoResponse emprestar(
            @RequestBody EmprestimoRequest req) {

        return service.emprestar(req);
    }

    @Operation(
        summary = "Listar empréstimos",
        description = "Retorna todos os empréstimos registrados. " +
                      "Permite filtrar os resultados por usuário, " +
                      "incluindo empréstimos ativos e devolvidos."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de empréstimos retornada com sucesso",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = EmprestimoResponse.class)
            )
        )
    )
    @GetMapping
    public List<EmprestimoResponse> listar(
            @Parameter(
                description = "Identificador do usuário para filtrar os empréstimos",
                example = "1"
            )
            @RequestParam(required = false) Long usuarioId) {

        return service.listar(usuarioId);
    }

    @Operation(
        summary = "Buscar empréstimo por ID",
        description = "Consulta os dados de um empréstimo, " +
                      "incluindo datas e situação atual."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Empréstimo encontrado",
            content = @Content(
                schema = @Schema(implementation = EmprestimoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Empréstimo não encontrado"
        )
    })
    @GetMapping("/{id}")
    public EmprestimoResponse buscar(
            @Parameter(
                description = "Identificador do empréstimo",
                example = "1"
            )
            @PathVariable Long id) {

        return service.buscar(id);
    }

    @Operation(
        summary = "Registrar devolução",
        description = "Registra a devolução de um empréstimo ativo. " +
                      "Libera a reserva correspondente ao livro por meio " +
                      "da chave UUID e atualiza a situação do empréstimo " +
                      "para DEVOLVIDO. Impede devoluções duplicadas."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Devolução registrada com sucesso",
            content = @Content(
                schema = @Schema(implementation = EmprestimoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Empréstimo ou livro não encontrado"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Empréstimo já devolvido ou conflito com a reserva"
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Serviço de livros indisponível"
        )
    })
    @PutMapping("/{id}/devolver")
    public EmprestimoResponse devolver(
            @Parameter(
                description = "Identificador do empréstimo",
                example = "1"
            )
            @PathVariable Long id) {

        return service.devolver(id);
    }

    @Operation(
        summary = "Excluir empréstimo",
        description = "Exclui um empréstimo do histórico somente quando " +
                      "ele já foi devolvido. Empréstimos ativos não podem " +
                      "ser excluídos."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Empréstimo excluído com sucesso",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Empréstimo não encontrado"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Empréstimo ainda está ativo"
        )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(
            @Parameter(
                description = "Identificador do empréstimo",
                example = "1"
            )
            @PathVariable Long id) {

        service.excluir(id);
    }

    @Operation(
        summary = "Verificar histórico de empréstimos de usuário",
        description = "Operação interna utilizada pelo serviço de usuários. " +
                      "Retorna true quando existe pelo menos um empréstimo " +
                      "registrado para o usuário, independentemente do status. " +
                      "Retorna false quando não existe nenhum registro."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Resultado da verificação do histórico",
        content = @Content(
            schema = @Schema(type = "boolean")
        )
    )
    @GetMapping("/usuario/{usuarioId}/existe")
    public boolean existeEmprestimoParaUsuario(
            @Parameter(
                description = "Identificador do usuário",
                example = "1"
            )
            @PathVariable Long usuarioId) {

        return service.existeEmprestimoParaUsuario(usuarioId);
    }
}