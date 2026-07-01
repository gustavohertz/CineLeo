# Cineleo Avaliações Service

Microserviço de gestão de avaliações (reviews) e notas de filmes do ecossistema **CineLeo**, desenvolvido com **Spring Boot 3**, **Spring Cloud Eureka Client** e **MongoDB**. 

Atua de forma independente e simplificada, validando a identidade do usuário através de tokens JWT emitidos pelo serviço de Autenticação.

---

## Sumário

* [Arquitetura](#arquitetura)
* [Estrutura do Projeto](#estrutura-do-projeto)
* [Tecnologias](#tecnologias)
* [Pré-requisitos](#pré-requisitos)
* [Configuração e Execução](#configuração-e-execução)
* [Endpoints e Exemplos cURL](#endpoints-e-exemplos-curl)
* [Segurança](#segurança)
* [Licença](#licença)

---

## Arquitetura

O serviço segue uma arquitetura baseada em microsserviços, utilizando **MongoDB** para alta flexibilidade de esquema e leitura rápida. O serviço se registra no **Spring Cloud Netflix Eureka** para descoberta.

### Componentes principais

* **Controllers** – Recebem requisições HTTP e delegam para os serviços.
    * `AvaliacaoController` – CRUD de avaliações.
    * `HealthCheckController` – Endpoint básico de verificação de status.

* **Services** – Lógica de negócio.
    * `AvaliacaoService` – Regras de criação, edição e exclusão. Impede avaliações duplicadas por filme.

* **Repository** – Camada de acesso a dados com Spring Data MongoDB.
    * `AvaliacaoRepository` – Extensão de `MongoRepository`.

* **Entity** – Coleção `avaliacoes` no MongoDB.

* **DTOs** – Objetos de transporte (Request e Response) validados com Bean Validation.

* **Config** – Configurações de Segurança (`SecurityConfig`) para validação de JWT via Resource Server sem estado.

---

## Estrutura do Projeto

```text
src/main/java/com/cineleo/avaliacoes/
├── AvaliacoesApplication.java        # Classe principal (@EnableDiscoveryClient)
├── config/
│   └── SecurityConfig.java           # Validação JWT e rotas públicas/privadas
├── controller/
│   ├── AvaliacaoController.java      # Endpoints principais
│   └── HealthCheckController.java    # /health-check
├── dto/
│   ├── AvaliacaoRequestDTO.java      # filmeId, nota, comentario
│   └── AvaliacaoResponseDTO.java     # Formato de saída padrão
├── entity/
│   └── Avaliacao.java                # Mapeamento do Documento MongoDB
├── exception/
│   ├── GlobalExceptionHandler.java   # Handler centralizado
│   └── ... (exceções de negócio)
├── repository/
│   └── AvaliacaoRepository.java      # MongoRepository
└── service/
    └── AvaliacaoService.java         # Regras de negócio
```

---

## Tecnologias

* **Java 21**
* **Spring Boot 3.4** (Web, Data MongoDB, Validation, Security, Actuator)
* **Spring Cloud Netflix Eureka Client**
* **MongoDB 7.0+**
* **Micrometer / OpenTelemetry** – Tracing Distribuído
* **Lombok**
* **Docker Compose**

---

## Pré-requisitos

* JDK 21
* Maven 3.8+
* Docker e Docker Compose (para o MongoDB)
* Servidor Eureka rodando (padrão: `http://localhost:8761/eureka/`)

---

## Configuração e Execução

### Variáveis de Ambiente / application.properties

```properties
spring.application.name=avaliacoes-service
server.port=8086

# MongoDB
spring.data.mongodb.uri=mongodb://avaliacoes_user:sua_senha@localhost:27017/avaliacoes_db?authSource=avaliacoes_db

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### Executando a Aplicação

1. Suba o contêiner do MongoDB.
2. Compile e execute o projeto com Maven:

```bash
mvn spring-boot:run
```

---

## Endpoints e Exemplos cURL

### 1. Criar Avaliação (Requer JWT)

```bash
curl -X POST http://localhost:8086/avaliacoes \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "filmeId": "100",
    "nota": 4.5,
    "comentario": "Excelente filme!"
  }'
```

### 2. Listar Avaliações de um Filme (Público)

```bash
curl -X GET http://localhost:8086/avaliacoes/filme/100
```

### 3. Listar Minhas Avaliações (Requer JWT)

```bash
curl -X GET http://localhost:8086/avaliacoes/minha \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

### 4. Editar Avaliação (Requer JWT - Somente Autor)

```bash
curl -X PUT http://localhost:8086/avaliacoes/ID_DA_AVALIACAO \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "filmeId": "100",
    "nota": 5.0,
    "comentario": "Mudei de ideia, é uma obra-prima."
  }'
```

### 5. Deletar Avaliação (Requer JWT - Somente Autor)

```bash
curl -X DELETE http://localhost:8086/avaliacoes/ID_DA_AVALIACAO \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

---

## Segurança

* O microsserviço atua como um **Resource Server** OAuth2.
* A validação do token é feita consultando as chaves públicas (JWKS) expostas pelo **Usuarios Service**.
* Apenas o endpoint `GET /avaliacoes/filme/**` é público; todos os outros exigem autenticação válida.
* O sistema previne que um usuário crie mais de uma avaliação para o mesmo filme e garante que apenas o autor possa editar ou excluir sua própria avaliação.

---

## Licença

Este projeto é parte do ecossistema Cineleo. Uso interno.
