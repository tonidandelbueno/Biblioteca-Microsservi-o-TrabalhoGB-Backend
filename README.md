# Biblioteca-Microsservi-o-TrabalhoGB-Backend
### Trabalho Grau B Programação Back-End
---
> Nomes: David Gabriel Moreira Xavier e Tainá Bueno Tonidandel

Sistema para controlar o acervo de uma biblioteca física: cadastrar leitores e livros e registrar empréstimos e devoluções. Cada parte é um microsserviço independente.

## Endpoints (todos via gateway, porta 8005)

**Usuário** — `/usuario`
- `POST /usuario` cadastra `{nome,email,senha}` → 201 (senha salva com BCrypt, nunca devolvida)
- `GET /usuario` · `GET /usuario/{id}` · `PUT /usuario/{id}` · `DELETE /usuario/{id}`
- `POST /usuario/login` `{email,senha}` → `{token}` (JWT)
- `GET /usuario/token/{token}` → `true/false`

**Livro** — `/livro`
- `POST /livro` `{titulo,autor,isbn}` → 201 · `GET /livro?titulo=` · `GET /livro/{id}` · `PUT /livro/{id}` · `DELETE /livro/{id}`
- `PUT /livro/{id}/disponibilidade?valor=true|false` (usado pelo emprestimo-service)

**Empréstimo** — `/emprestimo`
- `POST /emprestimo` `{usuarioId,livroId}` → 201 (valida leitor e livro, marca o livro como indisponível)
- `GET /emprestimo?usuarioId=` · `GET /emprestimo/{id}`
- `PUT /emprestimo/{id}/devolver` (libera o livro)
- `DELETE /emprestimo/{id}` (só se já devolvido)

> Os bancos H2 são em memória: os dados somem ao reiniciar o serviço. Console H2 de cada serviço: `http://localhost:<porta>/h2-console` (JDBC URL `jdbc:h2:mem:usuariodb`, `livrodb` ou `emprestimodb`; usuário `sa`, senha vazia).
