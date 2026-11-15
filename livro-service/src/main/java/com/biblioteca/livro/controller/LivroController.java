package com.biblioteca.livro.controller;

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

import com.biblioteca.livro.dto.LivroRequest;
import com.biblioteca.livro.dto.LivroResponse;
import com.biblioteca.livro.service.LivroService;

@RestController
@RequestMapping("/livro")
public class LivroController {

	private final LivroService service;

	public LivroController(LivroService service) {
		this.service = service;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public LivroResponse cadastrar(@RequestBody LivroRequest req) {
		return service.cadastrar(req);
	}

	@GetMapping
	public List<LivroResponse> listar(@RequestParam(required = false) String titulo) {
		return service.listar(titulo);
	}

	@GetMapping("/{id}")
	public LivroResponse buscar(@PathVariable Long id) {
		return service.buscar(id);
	}

	@PutMapping("/{id}")
	public LivroResponse atualizar(@PathVariable Long id, @RequestBody LivroRequest req) {
		return service.atualizar(id, req);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Long id) {
		service.excluir(id);
	}

	@PutMapping("/{id}/disponibilidade")
	public LivroResponse alterarDisponibilidade(@PathVariable Long id, @RequestParam boolean valor) {
		return service.alterarDisponibilidade(id, valor);
	}
}
