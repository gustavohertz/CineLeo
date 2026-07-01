# Cineleo Usuarios Service

Microserviço de gestão de usuários e autenticação, desenvolvido com **Spring Boot 3**, **Spring Cloud Eureka Client** e **PostgreSQL**. Emite tokens JWT utilizando o algoritmo **HS256**, que são validados pelo API Gateway por meio de uma chave secreta compartilhada.

---

## Sumário

* [Arquitetura](#arquitetura)
* [Estrutura do Projeto](#estrutura-do-projeto)
* [Tecnologias](#tecnologias)
* [Pré-requisitos](#pré-requisitos)
* [Configuração e Execução](#configuração-e-execução)

    * [Docker Compose – Banco de Dados](#docker-compose--banco-de-dados)
    * [Variáveis de Ambiente / application.properties](#variáveis-de-ambiente--applicationproperties)
    * [Executando a Aplicação](#executando-a-aplicação)
* [Endpoints e Exemplos cURL](#endpoints-e-exemplos-curl)
* [Segurança](#segurança)
* [Diagrama de Arquitetura](#diagrama-de-arquitetura)
* [Possíveis Melhorias Futuras](#possíveis-melhorias-futuras)
* [Licença](#licença)

---

## Arquitetura

O serviço segue uma arquitetura em camadas clássica do Spring Boot, expondo uma API REST e integrando-se ao ecossistema de microserviços via **Spring Cloud Netflix Eureka**.

### Componentes principais

* **Controllers** – Recebem requisições HTTP e delegam para os serviços.

    * `UsuarioController` – CRUD de usuários e login.

* **Services** – Lógica de negócio.

    * `UsuarioService` – Criação e consulta de usuários.
    * `AuthService` – Autenticação (e-mail/senha) e emissão de token.
    * `JwtService` – Geração e validação de tokens JWT utilizando HS256.

* **Repository** – Camada de acesso a dados com Spring Data JPA.

* **Entity** – Mapeamento objeto-relacional da tabela `usuarios` (schema `auth_service`).

* **DTOs** – Objetos de transporte de dados, com validação Bean Validation.

* **Exceptions** – Exceções customizadas e handler global (`GlobalExceptionHandler`).

* **Config** – Configuração do `PasswordEncoder` (BCrypt).

### Fluxo de autenticação

1. Cliente envia credenciais (`POST /usuarios/login`).
2. `AuthService` busca o usuário por e-mail, verifica se está ativo e compara a senha com o hash BCrypt armazenado.
3. Se as credenciais forem válidas, `JwtService` gera um token JWT assinado com HS256, contendo as claims `sub`, `name`, `email` e `roles`, com expiração de 1 hora.
4. O token é retornado no corpo da resposta.
5. Nas próximas requisições, o cliente envia o token no cabeçalho `Authorization: Bearer <token>`.
6. O API Gateway valida a assinatura e a expiração do token usando a mesma chave secreta configurada no serviço de usuários.

### Cadastro de usuário

* `POST /usuarios/create` recebe nome, idade, e-mail, CPF e senha.
* `UsuarioService` valida unicidade de e-mail e CPF, aplica hash na senha, define role padrão `USER` e persiste.

---

## Estrutura do Projeto

```text
src/main/java/com/cineleo/usuarios/
├── UsuariosApplication.java          # Classe principal, anotada com @EnableDiscoveryClient
├── config
│   └── SecurityConfig.java           # Bean PasswordEncoder (BCrypt)
├── controller
│   └── UsuarioController.java        # /usuarios/*
├── dto
│   ├── LoginReponseDTO.java          # accessToken, tokenType, expiresIn
│   ├── LoginRequestDTO.java          # email, senha (validação)
│   ├── UsuarioAutenticadoResponseDTO.java  # DTO interno para token validado
│   ├── UsuarioRequestDTO.java        # Dados de entrada para criação (inclui senha)
│   └── UsuarioResponseDTO.java       # Resposta sem informações sensíveis
├── entity
│   └── UsuarioEntity.java            # JPA entity mapeada para auth_service.usuarios
├── exception
│   ├── ConflictException.java
│   ├── CredenciaisInvalidasException.java
│   ├── GlobalExceptionHandler.java   # Handler global de exceções
│   └── ResourceNotFoundException.java
├── repository
│   └── UsuarioRepository.java        # Spring Data JPA repository
└── service
    ├── AuthService.java
    ├── JwtService.java               # Geração e validação de tokens JWT HS256
    └── UsuarioService.java

src/main/resources/
└── application.properties
```

---

## Tecnologias

* **Java 17+**
* **Spring Boot 3.x** (Web, Data JPA, Validation)
* **Spring Cloud Netflix Eureka Client**
* **PostgreSQL 16**
* **Auth0 java-jwt** – Biblioteca para JWT
* **Lombok** – Redução de boilerplate
* **BCrypt** – Hash de senhas
* **Docker Compose** – Infraestrutura local

---

## Pré-requisitos

* JDK 17 ou superior
* Maven 3.8+ (ou Gradle, se preferir)
* Docker e Docker Compose (para o banco de dados)
* Servidor Eureka rodando (padrão: `http://localhost:8761/eureka/`)

---

## Configuração e Execução

### Docker Compose – Banco de Dados

Crie o arquivo `docker-compose.yml` na raiz do projeto (ou utilize o já existente):

```yaml
services:
  usuarios-db:
    image: postgres:16-alpine
    container_name: cineleo-usuarios-db
    environment:
      POSTGRES_DB: usuarios_db
      POSTGRES_USER: usuarios_user
      POSTGRES_PASSWORD: usuarios_pass
    ports:
      - "5435:5432"
    volumes:
      - usuarios_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U usuarios_user -d usuarios_db"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

volumes:
  usuarios_data:
    driver: local
```

Subir o banco:

```bash
docker-compose up -d
```

### Variáveis de Ambiente / application.properties

```properties
spring.application.name=usuarios-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

server.port=8083

# JWT
jwt.secret=${JWT_SECRET:cineleo-chave-secreta-local-123456789012345}
jwt.expiration-seconds=3600

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5435/usuarios_db
spring.datasource.username=usuarios_user
spring.datasource.password=usuarios_pass
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.hbm2ddl.create_namespaces=true

# Jackson
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.time-zone=America/Sao_Paulo
```

**Nota:** O valor de `jwt.secret` deve ser o mesmo configurado no API Gateway. Em ambiente local, o valor após `:` funciona como chave padrão; em outros ambientes, recomenda-se definir a variável `JWT_SECRET`.

### Executando a Aplicação

1. Certifique-se de que o banco de dados está rodando (`docker-compose up -d`).
2. Compile e execute o projeto com Maven:

```bash
./mvnw spring-boot:run
```

Ou via IDE (IntelliJ, Eclipse) executando a classe `UsuariosApplication`.

Após a inicialização, o Hibernate criará automaticamente o schema `auth_service` e as tabelas `usuarios` e `usuario_roles`.

---

## Endpoints e Exemplos cURL

### 1. Cadastro de Usuário

```bash
curl -X POST http://localhost:8083/usuarios/create \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "idade": 30,
    "email": "joao@email.com",
    "cpf": "12345678901",
    "senha": "minhaSenha123"
  }'
```

**Response**

```json
{
  "id": 1,
  "nome": "João Silva",
  "idade": 30,
  "email": "joao@email.com",
  "cpf": "12345678901",
  "ativo": true,
  "criadoEm": "2026-06-17T22:30:00",
  "roles": ["USER"]
}
```

---

### 2. Listar Todos os Usuários

```bash
curl -X GET http://localhost:8083/usuarios/all
```

---

### 3. Buscar Usuário por ID

```bash
curl -X GET http://localhost:8083/usuarios/1
```

---

### 4. Login e Obtenção de Token

```bash
curl -X POST http://localhost:8083/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "senha": "minhaSenha123"
  }'
```

**Response**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

## Segurança

* Senhas armazenadas exclusivamente como hash BCrypt.
* Tokens JWT assinados com o algoritmo **HS256**.
* A chave secreta não é enviada ao cliente nem exposta por endpoint.
* O API Gateway valida a assinatura, o emissor e a expiração do token.
* Validação de entrada utilizando Bean Validation.
* Tratamento centralizado de exceções.
* Em produção, a chave deve ser armazenada em variável de ambiente ou gerenciador de segredos.

---

## Diagrama de Arquitetura

```text
+-----------------+       (1) POST /usuarios/create       +-----------------------+
|    Cliente      | ----------------------------------> |    UsuarioService     |
|                 | <---------------------------------- |                       |
+-----------------+      201 Created                    +-----------+-----------+
                                                                |
                                                        +-------v--------+
                                                        | UsuarioRepository|
                                                        +-------+--------+
                                                                |
                                                        +-------v--------+
                                                        |    PostgreSQL   |
                                                        | (usuarios_db)   |
                                                        +----------------+

+-----------------+       (2) POST /usuarios/login        +-----------------------+
|    Cliente      | ----------------------------------> |      AuthService      |
|                 | <---------------------------------- |                       |
+-----------------+     200 OK {accessToken}            +-----------+-----------+
                                                                |
                                                        +-------v--------+
                                                        |   JwtService    |
                                                        |     HS256       |
                                                        +-------+--------+
                                                                |
                                                        +-------v--------+
                                                        |   Token JWT    |
                                                        +----------------+

+-----------------+  (3) Authorization: Bearer <token>   +-------------------+
|    Cliente      | ----------------------------------> |    API Gateway    |
|                 |                                    | valida com HS256  |
|                 | <---------------------------------- | ou encaminha      |
+-----------------+        Resposta da requisição        +-------------------+

+-----------------+
| Eureka Server   |
| registro serviço|
+-----------------+
```

---

## Possíveis Melhorias Futuras

* Implementar refresh token.
* Adicionar Spring Security.
* Suporte a múltiplas roles.
* Exclusão lógica de usuários.
* Atualização parcial (PATCH).
* Testes com JUnit e Testcontainers.
* Observabilidade com Micrometer e Prometheus.
* Documentação OpenAPI/Swagger.

---

## Licença

Este projeto é parte do ecossistema Cineleo. Uso interno.