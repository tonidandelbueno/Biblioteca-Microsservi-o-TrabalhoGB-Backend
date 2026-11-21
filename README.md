# Biblioteca — Sistema de Microsserviços

### Trabalho Grau B — Programação Back-End

**Integrantes:** David Gabriel Moreira Xavier e Tainá Bueno Tonidandel.

Sistema de gerenciamento de biblioteca física desenvolvido em Java e Spring Boot, com funcionalidades de cadastro de usuários, catálogo de livros, empréstimos e devoluções. A aplicação é organizada em seis microsserviços independentes, com descoberta de serviços pelo Eureka, configurações centralizadas e roteamento pelo API Gateway.

## Tecnologias utilizadas

- **Java 21** e **Maven**
- **Spring Boot** e **Spring Cloud**
- **Spring Web MVC** nos serviços de negócio; **Spring Cloud Gateway (WebFlux)** no Gateway
- **Netflix Eureka** para registro e descoberta de serviços
- **Spring Cloud Config Server** para centralização das configurações
- **Spring Data JPA / Hibernate** e **H2 em memória**
- **Spring Security Crypto (BCrypt)** para armazenamento do hash das senhas
- **JWT** para autenticação básica no Gateway
- **Springdoc OpenAPI / Swagger UI** para documentação das APIs
- **Lombok** para geração de getters e setters nas entidades JPA

## Microsserviços e portas

| Serviço | Porta | Responsabilidade |
| --- | ---: | --- |
| `registry-service` | **8761** | Registro e descoberta de serviços com Eureka |
| `config-service` | **8006** | Fornecimento das configurações centralizadas |
| `usuario-service` | **8004** | Usuários, login e validação de JWT |
| `livro-service` | **8001** | Catálogo e controle de reservas de livros |
| `emprestimo-service` | **8003** | Empréstimos, devoluções e histórico |
| `gateway-service` | **8005** | Entrada principal das APIs e autenticação JWT |

### Ordem de inicialização

1. **`registry-service`** — inicia o Eureka na porta `8761`.
2. **`config-service`** — disponibiliza as configurações na porta `8006`.
3. **`usuario-service`** — inicia a API de usuários na porta `8004`.
4. **`livro-service`** — inicia a API de livros na porta `8001`.
5. **`emprestimo-service`** — inicia a API de empréstimos na porta `8003`; depende das APIs de usuários e livros para operações de negócio.
6. **`gateway-service`** — inicia a entrada principal na porta `8005` e consulta o serviço de usuários para validar JWTs.

Para conferir o registro dos serviços, abra o painel do Eureka:

- **Eureka:** http://localhost:8761/

### Documentação Swagger / OpenAPI

**Após iniciar os serviços na ordem indicada acima, é possível acessar a documentação HTML das três APIs:**

1. **Usuários:** http://localhost:8004/swagger-ui.html
2. **Livros:** http://localhost:8001/swagger-ui.html
3. **Empréstimos:** http://localhost:8003/swagger-ui.html

A URL-base das operações externas é **`http://localhost:8005`**.

### Exemplos de rotas

**Usuário** — `/usuario`
- `POST /usuario` cadastra `{nome,email,senha}` → 201 (senha armazenada com BCrypt e não retornada).
- `GET /usuario` · `GET /usuario/{id}` · `PUT /usuario/{id}` · `DELETE /usuario/{id}`.
- `POST /usuario/login` com `{email,senha}` → `{token}` (JWT).
- `GET /usuario/token/{token}` → `true/false` (validação interna utilizada pelo Gateway).

**Livro** — `/livro`
- `POST /livro` cadastra `{titulo,autor,isbn}` → 201.
- `GET /livro?titulo=` · `GET /livro/{id}` · `PUT /livro/{id}` · `DELETE /livro/{id}`.
- `PUT /livro/{id}/reservar?chave={uuid}` · `PUT /livro/{id}/liberar?chave={uuid}` (operações internas utilizadas pelo serviço de empréstimos).

**Empréstimo** — `/emprestimo`
- `POST /emprestimo` cadastra `{usuarioId,livroId}` → 201 (verifica o usuário e reserva o livro).
- `GET /emprestimo?usuarioId=` · `GET /emprestimo/{id}`.
- `PUT /emprestimo/{id}/devolver` (registra a devolução e libera o livro).
- `DELETE /emprestimo/{id}` (somente empréstimos devolvidos).
- `GET /emprestimo/usuario/{usuarioId}/existe` → `true/false` (consulta interna utilizada pelo serviço de usuários).

**Autenticação:** pelo Gateway, `POST /usuario` e `POST /usuario/login` são públicos. As demais operações exigem o cabeçalho `Authorization: Bearer <token>`. Os links do Swagger acessam diretamente cada serviço, sem passar pelo filtro JWT do Gateway.
