package com.biblioteca.usuario.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.biblioteca.usuario.client.EmprestimoClient;
import com.biblioteca.usuario.dto.LoginRequest;
import com.biblioteca.usuario.dto.TokenResponse;
import com.biblioteca.usuario.dto.UsuarioRequest;
import com.biblioteca.usuario.dto.UsuarioResponse;
import com.biblioteca.usuario.model.Usuario;
import com.biblioteca.usuario.repository.UsuarioRepository;

@Service
public class UsuarioService {

	private final UsuarioRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final EmprestimoClient emprestimoClient;
	
	public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService,EmprestimoClient emprestimoClient) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.emprestimoClient = emprestimoClient;
	}

	public UsuarioResponse cadastrar(UsuarioRequest req) {
		validar(req);
		if (repository.existsByEmail(req.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com este e-mail");
		}
		Usuario u = new Usuario();
		u.setNome(req.nome());
		u.setEmail(req.email());
		u.setSenha(passwordEncoder.encode(req.senha()));
		return UsuarioResponse.from(repository.save(u));
	}

	public List<UsuarioResponse> listar() {
		return repository.findAll().stream().map(UsuarioResponse::from).toList();
	}

	public UsuarioResponse buscar(Long id) {
		return UsuarioResponse.from(buscarEntidade(id));
	}

	public UsuarioResponse atualizar(Long id, UsuarioRequest req) {
		validar(req);
		Usuario u = buscarEntidade(id);
		repository.findByEmail(req.email()).ifPresent(outro -> {
			if (!outro.getId().equals(id)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com este e-mail");
			}
		});
		u.setNome(req.nome());
		u.setEmail(req.email());
		u.setSenha(passwordEncoder.encode(req.senha()));
		return UsuarioResponse.from(repository.save(u));
	}
	//novo excluir garante só user sem emprestimos(ativo ou devolvido) para manter registro
	public void excluir(Long id) {
	    Usuario usuario = buscarEntidade(id);
	    boolean possuiEmprestimos =
	        emprestimoClient.existeEmprestimoParaUsuario(id);
	    if (possuiEmprestimos) {
	        throw new ResponseStatusException(
	            HttpStatus.CONFLICT,
	            "Usuário possui empréstimos registrados"
	        );
	    }
	    repository.delete(usuario);
	}

	public TokenResponse login(LoginRequest req) {
		Usuario u = repository.findByEmail(req.email())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos"));
		if (req.senha() == null || !passwordEncoder.matches(req.senha(), u.getSenha())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
		}
		return new TokenResponse(jwtService.generateToken(u.getEmail()));
	}

	public boolean tokenValido(String token) {
		return jwtService.isValid(token);
	}

	private Usuario buscarEntidade(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
	}

	private void validar(UsuarioRequest req) {
		if (req.nome() == null || req.nome().isBlank() || req.email() == null || req.email().isBlank()
				|| req.senha() == null || req.senha().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nome, email e senha são obrigatórios");
		}
	}
}
