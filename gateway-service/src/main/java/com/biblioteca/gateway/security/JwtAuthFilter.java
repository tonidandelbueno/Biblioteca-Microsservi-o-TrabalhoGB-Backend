package com.biblioteca.gateway.security;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

public class JwtAuthFilter implements WebFilter {
    private final WebClient.Builder webClientBuilder;
    public JwtAuthFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain) {
        String path = exchange.getRequest()
                .getURI().getPath();
        HttpMethod metodo = exchange.getRequest().getMethod();

        // Cadastro e login são públicos
        if (HttpMethod.POST.equals(metodo)
                && (path.equals("/usuario")
                    || path.equals("/usuario/login"))) {
            return chain.filter(exchange);
        }
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // Verifica se existe Bearer Token
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")
                || authHeader.substring(7).isBlank()) {
            return naoAutorizado(exchange);
        }
        String token = authHeader.substring(7);

        // Consulta o usuario-service
        return webClientBuilder.build()
                .get()
                .uri("http://USUARIO-SERVICE/usuario/token/{token}",token)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(5))
                .defaultIfEmpty(false)
                .onErrorReturn(false)
                .flatMap(valido -> {
                    if (Boolean.TRUE.equals(valido)) {
                        return chain.filter(exchange);
                    }
                    return naoAutorizado(exchange);
                });
    }
    private Mono<Void> naoAutorizado(
            ServerWebExchange exchange) {
        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
