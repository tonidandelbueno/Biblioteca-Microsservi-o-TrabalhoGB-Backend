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

@RestController
@RequestMapping("/emprestimo")
public class EmprestimoController {

	private final EmprestimoService service;

	public EmprestimoController(EmprestimoService service) {
		this.service = service;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EmprestimoResponse emprestar(@RequestBody EmprestimoRequest req) {
		return service.emprestar(req);
	}

	@GetMapping
	public List<EmprestimoResponse> listar(@RequestParam(required = false) Long usuarioId) {
		return service.listar(usuarioId);
	}

	@GetMapping("/{id}")
	public EmprestimoResponse buscar(@PathVariable Long id) {
		return service.buscar(id);
	}

	@PutMapping("/{id}/devolver")
	public EmprestimoResponse devolver(@PathVariable Long id) {
		return service.devolver(id);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Long id) {
		service.excluir(id);
	}
		
	@GetMapping("/usuario/{usuarioId}/existe")
	public boolean existeEmprestimoParaUsuario(
	        @PathVariable Long usuarioId) {
	    return service.existeEmprestimoParaUsuario(usuarioId);
	}
}
