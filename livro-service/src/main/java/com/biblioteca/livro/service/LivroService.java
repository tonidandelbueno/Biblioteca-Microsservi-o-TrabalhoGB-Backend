package com.biblioteca.livro.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.biblioteca.livro.dto.LivroRequest;
import com.biblioteca.livro.dto.LivroResponse;
import com.biblioteca.livro.model.Livro;
import com.biblioteca.livro.repository.LivroRepository;

@Service
public class LivroService {

	private final LivroRepository repository;

	public LivroService(LivroRepository repository) {
		this.repository = repository;
	}

	public LivroResponse cadastrar(LivroRequest req) {
		validar(req);
		Livro l = new Livro();
		l.setTitulo(req.titulo());
		l.setAutor(req.autor());
		l.setIsbn(req.isbn());
		l.setDisponivel(true);
		return LivroResponse.from(repository.save(l));
	}

	public List<LivroResponse> listar(String titulo) {
		List<Livro> livros = (titulo == null || titulo.isBlank())
				? repository.findAll()
				: repository.findByTituloContainingIgnoreCase(titulo);
		return livros.stream().map(LivroResponse::from).toList();
	}

	public LivroResponse buscar(Long id) {
		return LivroResponse.from(buscarEntidade(id));
	}

	public LivroResponse atualizar(Long id, LivroRequest req) {
		validar(req);
		Livro l = buscarEntidade(id);
		l.setTitulo(req.titulo());
		l.setAutor(req.autor());
		l.setIsbn(req.isbn());
		return LivroResponse.from(repository.save(l));
	}

	public void excluir(Long id) {
		Livro l = buscarEntidade(id);
		if (!l.isDisponivel()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Livro emprestado não pode ser excluído");
		}
		repository.delete(l);
	}

	// Chamado pelo emprestimo-service ao emprestar (false) e ao devolver (true)
	public LivroResponse alterarDisponibilidade(Long id, boolean valor) {
		Livro l = buscarEntidade(id);
		l.setDisponivel(valor);
		return LivroResponse.from(repository.save(l));
	}

	private Livro buscarEntidade(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado"));
	}

	private void validar(LivroRequest req) {
		if (req.titulo() == null || req.titulo().isBlank() || req.autor() == null || req.autor().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "titulo e autor são obrigatórios");
		}
	}
}
