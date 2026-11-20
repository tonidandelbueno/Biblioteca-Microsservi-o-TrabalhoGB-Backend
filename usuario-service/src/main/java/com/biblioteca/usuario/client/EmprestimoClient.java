package com.biblioteca.usuario.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Component
public class EmprestimoClient {

    private static final String URL = "http://EMPRESTIMO-SERVICE/emprestimo/";
    private final RestTemplate restTemplate;
    public EmprestimoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public boolean existeEmprestimoParaUsuario(Long usuarioId) {
        try {
            Boolean existe = restTemplate.getForObject(
                URL + "usuario/{usuarioId}/existe",
                Boolean.class,
                usuarioId
            );

            if (existe == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Resposta inválida do emprestimo-service"
                );
            }

            return existe;

        } catch (RestClientException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"emprestimo-service indisponível",
                e
            );
        }
    }
}
