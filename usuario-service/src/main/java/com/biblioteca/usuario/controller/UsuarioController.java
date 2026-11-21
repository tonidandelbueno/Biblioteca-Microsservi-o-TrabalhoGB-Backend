package com.biblioteca.usuario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.usuario.dto.LoginRequest;
import com.biblioteca.usuario.dto.TokenResponse;
import com.biblioteca.usuario.dto.UsuarioRequest;
import com.biblioteca.usuario.dto.UsuarioResponse;
import com.biblioteca.usuario.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/usuario")
@Tag(
    name = "Usuários",
    description = "Gerenciamento de usuários e autenticação da biblioteca"
)
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(
        summary = "Cadastrar usuário",
        description = "Cadastra um novo usuário com nome, e-mail e senha. " +
                      "A senha é armazenada de forma criptografada."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Usuário cadastrado com sucesso",
            content = @Content(
                schema = @Schema(implementation = UsuarioResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados obrigatórios não informados"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "E-mail já cadastrado"
        )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@RequestBody UsuarioRequest req) {
        return service.cadastrar(req);
    }

    @Operation(
        summary = "Listar usuários",
        description = "Retorna todos os usuários cadastrados, " +
                      "sem expor suas senhas."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de usuários retornada com sucesso",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = UsuarioResponse.class)
            )
        )
    )
    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    @Operation(
        summary = "Buscar usuário por ID",
        description = "Consulta as informações de um usuário cadastrado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Usuário encontrado",
            content = @Content(
                schema = @Schema(implementation = UsuarioResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado"
        )
    })
    @GetMapping("/{id}")
    public UsuarioResponse buscar(
            @Parameter(
                description = "Identificador do usuário",
                example = "1"
            )
            @PathVariable Long id) {

        return service.buscar(id);
    }

    @Operation(
        summary = "Atualizar usuário",
        description = "Atualiza nome, e-mail e senha de um usuário. " +
                      "O novo e-mail não pode estar associado a outro usuário."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Usuário atualizado com sucesso",
            content = @Content(
                schema = @Schema(implementation = UsuarioResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados obrigatórios não informados"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "E-mail já utilizado por outro usuário"
        )
    })
    @PutMapping("/{id}")
    public UsuarioResponse atualizar(
            @Parameter(
                description = "Identificador do usuário",
                example = "1"
            )
            @PathVariable Long id,
            @RequestBody UsuarioRequest req) {

        return service.atualizar(id, req);
    }

    @Operation(
        summary = "Excluir usuário",
        description = "Exclui somente usuários que não possuem " +
                      "empréstimos registrados, ativos ou devolvidos. " +
                      "A exclusão depende de uma consulta ao serviço " +
                      "de empréstimos."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Usuário excluído com sucesso",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Usuário possui empréstimos registrados"
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Serviço de empréstimos indisponível"
        )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(
            @Parameter(
                description = "Identificador do usuário",
                example = "1"
            )
            @PathVariable Long id) {

        service.excluir(id);
    }

    @Operation(
        summary = "Autenticar usuário",
        description = "Valida o e-mail e a senha informados. " +
                      "Quando as credenciais são corretas, " +
                      "retorna um token JWT para autenticação."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(
                schema = @Schema(implementation = TokenResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "E-mail ou senha inválidos"
        )
    })
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req) {
        return service.login(req);
    }

    @Operation(
        summary = "Validar token JWT",
        description = "Operação interna utilizada pelo API Gateway. " +
                      "Retorna true quando o token é válido e false " +
                      "quando é inválido ou expirou."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Resultado da validação do token",
        content = @Content(
            schema = @Schema(type = "boolean")
        )
    )
    @GetMapping("/token/{token}")
    public boolean tokenValido(
            @Parameter(
                description = "Token JWT que será validado"
            )
            @PathVariable String token) {

        return service.tokenValido(token);
    }
}
