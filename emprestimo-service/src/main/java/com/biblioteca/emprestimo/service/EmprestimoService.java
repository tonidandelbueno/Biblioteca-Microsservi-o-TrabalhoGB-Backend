package com.biblioteca.emprestimo.service;

import java.util.UUID;
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

	public EmprestimoResponse emprestar(EmprestimoRequest req) {
	
	    if (req.usuarioId() == null || req.livroId() == null) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "usuarioId e livroId são obrigatórios"
	        );
	    }
	
	    // Verifica a existência do usuário
	    usuarioClient.buscar(req.usuarioId());
	
	    // Gera uma chave única para o empréstimo
	    UUID chaveReserva = UUID.randomUUID();
	
	    // Reserva o livro antes de salvar o empréstimo
	    livroClient.reservar(req.livroId(), chaveReserva);
	
	    Emprestimo e = new Emprestimo();
	    e.setUsuarioId(req.usuarioId());
	    e.setLivroId(req.livroId());
	    e.setChaveReserva(chaveReserva);
	    e.setDataEmprestimo(LocalDate.now());
	    e.setDataPrevistaDevolucao(
	        LocalDate.now().plusDays(prazoDias));
	    e.setStatus(StatusEmprestimo.ATIVO);
	
	    Emprestimo salvo;
	
	    try {
	        salvo = repository.saveAndFlush(e);
	    } catch (RuntimeException erro) {
	        // Tenta desfazer a reserva se a gravação falhar
	        try {
	            livroClient.liberar(req.livroId(),chaveReserva);
	        } catch (RuntimeException falhaLiberacao) {
	            erro.addSuppressed(falhaLiberacao);
	        }
	        throw erro;
	    }
	    return EmprestimoResponse.from(salvo);
	}

	public List<EmprestimoResponse> listar(Long usuarioId) {
		List<Emprestimo> lista = (usuarioId == null) ? repository.findAll() : repository.findByUsuarioId(usuarioId);
		return lista.stream().map(EmprestimoResponse::from).toList();
	}
	
	public boolean existeEmprestimoParaUsuario(Long usuarioId) {
        return repository.existsByUsuarioId(usuarioId);
    }
	public EmprestimoResponse buscar(Long id) {
		return EmprestimoResponse.from(buscarEntidade(id));
	}


	@Transactional
	public EmprestimoResponse devolver(Long id) {
	
	    Emprestimo e = repository.buscarParaDevolucao(id)
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Empréstimo não encontrado"));
	    if (e.getStatus() != StatusEmprestimo.ATIVO) {
	        throw new ResponseStatusException(HttpStatus.CONFLICT,"Este empréstimo já foi devolvido");
	    }
	    if (e.getChaveReserva() == null) {
	        throw new ResponseStatusException(HttpStatus.CONFLICT,"Empréstimo sem chave de reserva");
	    }
	
	    // Libera somente a reserva correspondente
	    livroClient.liberar(
	        e.getLivroId(),
	        e.getChaveReserva());
	
	    // Atualiza o empréstimo após a liberação
	    e.setStatus(StatusEmprestimo.DEVOLVIDO);
	    e.setDataDevolucao(LocalDate.now());
	    Emprestimo salvo = repository.save(e);
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
