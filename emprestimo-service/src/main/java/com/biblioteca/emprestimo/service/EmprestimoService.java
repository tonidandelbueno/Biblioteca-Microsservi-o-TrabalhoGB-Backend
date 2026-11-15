package com.biblioteca.emprestimo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.biblioteca.emprestimo.client.LivroClient;
import com.biblioteca.emprestimo.client.UsuarioClient;
import com.biblioteca.emprestimo.dto.EmprestimoRequest;
import com.biblioteca.emprestimo.dto.EmprestimoResponse;
import com.biblioteca.emprestimo.dto.LivroDto;
import com.biblioteca.emprestimo.model.Emprestimo;
import com.biblioteca.emprestimo.model.StatusEmprestimo;
import com.biblioteca.emprestimo.repository.EmprestimoRepository;

@Service
public class EmprestimoService {

	private final EmprestimoRepository repository;
	private final UsuarioClient usuarioClient;
	private final LivroClient livroClient;
	private final int prazoDias;

	public EmprestimoService(EmprestimoRepository repository, UsuarioClient usuarioClient, LivroClient livroClient,
			@Value("${emprestimo.prazo-dias:14}") int prazoDias) {
		this.repository = repository;
		this.usuarioClient = usuarioClient;
		this.livroClient = livroClient;
		this.prazoDias = prazoDias;
	}
	@Transactional
	public EmprestimoResponse emprestar(EmprestimoRequest req) {
		if (req.usuarioId() == null || req.livroId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId e livroId são obrigatórios");
		}
		usuarioClient.buscar(req.usuarioId());
		LivroDto livro = livroClient.buscar(req.livroId());
		if (!livro.disponivel()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Livro indisponível (já emprestado)");
		}

		Emprestimo e = new Emprestimo();
		e.setUsuarioId(req.usuarioId());
		e.setLivroId(req.livroId());
		e.setDataEmprestimo(LocalDate.now());
		e.setDataPrevistaDevolucao(LocalDate.now().plusDays(prazoDias));
		e.setStatus(StatusEmprestimo.ATIVO);
		Emprestimo salvo = repository.save(e);

		livroClient.alterarDisponibilidade(req.livroId(), false);
		return EmprestimoResponse.from(salvo);
	}

	public List<EmprestimoResponse> listar(Long usuarioId) {
		List<Emprestimo> lista = (usuarioId == null) ? repository.findAll() : repository.findByUsuarioId(usuarioId);
		return lista.stream().map(EmprestimoResponse::from).toList();
	}

	public EmprestimoResponse buscar(Long id) {
		return EmprestimoResponse.from(buscarEntidade(id));
	}

	@Transactional
	public EmprestimoResponse devolver(Long id) {
		Emprestimo e = buscarEntidade(id);
		if (e.getStatus() == StatusEmprestimo.DEVOLVIDO) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Este empréstimo já foi devolvido");
		}
		e.setStatus(StatusEmprestimo.DEVOLVIDO);
		e.setDataDevolucao(LocalDate.now());
		Emprestimo salvo = repository.save(e);

		livroClient.alterarDisponibilidade(e.getLivroId(), true);
		return EmprestimoResponse.from(salvo);
	}

	public void excluir(Long id) {
		Emprestimo e = buscarEntidade(id);
		if (e.getStatus() == StatusEmprestimo.ATIVO) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Devolva o livro antes de excluir o empréstimo");
		}
		repository.delete(e);
	}

	private Emprestimo buscarEntidade(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empréstimo não encontrado"));
	}
}
