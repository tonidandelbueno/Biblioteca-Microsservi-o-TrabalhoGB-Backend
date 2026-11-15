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

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	private final UsuarioService service;

	public UsuarioController(UsuarioService service) {
		this.service = service;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioResponse cadastrar(@RequestBody UsuarioRequest req) {
		return service.cadastrar(req);
	}

	@GetMapping
	public List<UsuarioResponse> listar() {
		return service.listar();
	}

	@GetMapping("/{id}")
	public UsuarioResponse buscar(@PathVariable Long id) {
		return service.buscar(id);
	}

	@PutMapping("/{id}")
	public UsuarioResponse atualizar(@PathVariable Long id, @RequestBody UsuarioRequest req) {
		return service.atualizar(id, req);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Long id) {
		service.excluir(id);
	}

	@PostMapping("/login")
	public TokenResponse login(@RequestBody LoginRequest req) {
		return service.login(req);
	}

	@GetMapping("/token/{token}")
	public boolean tokenValido(@PathVariable String token) {
		return service.tokenValido(token);
	}
}
