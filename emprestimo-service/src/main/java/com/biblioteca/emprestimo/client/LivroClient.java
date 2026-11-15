package com.biblioteca.emprestimo.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.biblioteca.emprestimo.dto.LivroDto;


@Component
public class LivroClient {

	private static final String URL = "http://LIVRO-SERVICE/livro/";

	private final RestTemplate restTemplate;

	public LivroClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public LivroDto buscar(Long id) {
		try {
			return restTemplate.getForObject(URL + id, LivroDto.class);
		} catch (HttpClientErrorException.NotFound e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado");
		} catch (RestClientException | IllegalStateException e) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "livro-service indisponível");
		}
	}

	public void alterarDisponibilidade(Long id, boolean disponivel) {
		try {
			restTemplate.put(URL + "{id}/disponibilidade?valor={valor}", null, id, disponivel);
		} catch (HttpClientErrorException.NotFound e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado");
		} catch (RestClientException | IllegalStateException e) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "livro-service indisponível");
		}
	}
}
