package com.biblioteca.emprestimo.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.biblioteca.emprestimo.dto.UsuarioDto;

@Component
public class UsuarioClient {

	private static final String URL = "http://USUARIO-SERVICE/usuario/";

	private final RestTemplate restTemplate;

	public UsuarioClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public UsuarioDto buscar(Long id) {
	    try {
	        UsuarioDto usuario = restTemplate.getForObject(URL + id, UsuarioDto.class);
	        if (usuario == null) {
	            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Resposta vazia do usuario-service");
	        }
	        return usuario;
	    } catch (HttpClientErrorException.NotFound e) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
	    } catch (RestClientException | IllegalStateException e) {
	        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "usuario-service indisponível");
	    }
	}

}
