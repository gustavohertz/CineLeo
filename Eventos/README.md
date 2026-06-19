# Cineleo Eventos Service

Microserviço responsável pela gestão de filmes, salas, sessões e reservas de ingressos no ecossistema Cineleo. Desenvolvido com **Spring Boot 3**, **Spring Cloud Eureka Client** e **PostgreSQL**. Ele se comunica com os serviços de `Usuarios` para validar clientes e `Pagamento` para processar transações financeiras, além de emitir eventos via **Apache Kafka**.

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
* [Integração e Eventos](#integração-e-eventos)
* [Diagrama de Arquitetura](#diagrama-de-arquitetura)
* [Possíveis Melhorias Futuras](#possíveis-melhorias-futuras)
* [Licença](#licença)

---

## Arquitetura

O serviço segue uma arquitetura em camadas do Spring Boot, expondo uma API REST e integrando-se via REST (Feign/RestTemplate) e mensageria (Kafka).

### Componentes principais

* **Controllers** – Endpoints para gerenciamento do cinema:
    * `FilmeController` (`/filmes`) – CRUD de filmes.
    * `SalaController` (`/salas`) – Gestão das salas do cinema.
    * `SessaoController` (`/sessoes`) – Horários e alocação de filmes nas salas.
    * `ReservaController` (`/reservas`) – Criação de reservas de ingressos e pagamentos.

* **Services** – Contêm a lógica de negócio principal para evitar concorrência nas sessões e validação de disponibilidade.

* **Repository** – Camada de acesso a dados com Spring Data JPA para as entidades `Filme`, `Sala`, `Sessao` e `Reserva`.

* **Client/Integrações** – Chamadas para `usuarios-service` e `pagamento-service`.

* **Kafka Producer** – Envia eventos de novas reservas confirmadas ou falhas para tópicos Kafka.

---

## Estrutura do Projeto

```text
src/main/java/com/cineleo/eventos/
├── EventosApplication.java           # Classe principal (@EnableDiscoveryClient)
├── cli
├── client                            # Clientes REST (ex: FeignClient) para outros serviços
├── config                            # Configurações do Kafka e Beans
├── controller                        # Endpoints da API
│   ├── FilmeController.java
│   ├── ReservaController.java
│   ├── SalaController.java
│   └── SessaoController.java
├── dto                               # Objetos de transferência (Request/Response)
├── entity                            # Entidades JPA (Filme, Sala, Sessao, Reserva)
├── exception                         # Handlers globais e exceções customizadas
├── repository                        # Repositórios Spring Data JPA
└── service                           # Regras de negócio e envio de mensagens
```

---

## Tecnologias

* **Java 21**
* **Spring Boot 3.4.0** (Web, Data JPA, Validation)
* **Spring Cloud Netflix Eureka Client**
* **Spring Kafka** (Integração com Apache Kafka)
* **PostgreSQL 16**
* **Lombok**
* **Docker Compose**

---

## Pré-requisitos

* JDK 21
* Maven 3.8+ (ou usar o Wrapper)
* Docker e Docker Compose
* Servidor Eureka rodando (padrão: `http://localhost:8761/eureka/`)
* Apache Kafka rodando localmente (padrão: `localhost:9092`)
* Serviços dependentes: `usuarios-service` e `pagamento-service`

---

## Configuração e Execução

### Docker Compose – Banco de Dados

Suba o banco de dados PostgreSQL especificado no `docker-compose.yml`:

```bash
docker-compose up -d
```

O container `cineleo-eventos-db` rodará na porta `5434`.

### Variáveis de Ambiente / application.properties

O serviço roda por padrão na porta **8082** e se conecta ao Eureka em `http://localhost:8761/eureka/`.

```properties
spring.application.name=eventos-service
server.port=8082

# Banco de Dados
spring.datasource.url=jdbc:postgresql://localhost:5434/eventos_db
spring.datasource.username=eventos_user
spring.datasource.password=eventos_pass

# Serviços externos
services.usuarios.url=http://localhost:8083
services.pagamento.url=http://localhost:5000

# Kafka
spring.kafka.producer.bootstrap-servers=localhost:9092
```

### Executando a Aplicação

Certifique-se de que o DB e o Kafka estão rodando e execute:

```bash
./mvnw spring-boot:run
```

O Hibernate criará as tabelas automaticamente.

---

## Endpoints e Exemplos cURL

### 1. Criar um Filme

```bash
curl -X POST http://localhost:8082/filmes \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "O Senhor dos Anéis",
    "duracaoMinutos": 200,
    "genero": "Fantasia",
    "classificacaoIndicativa": 12,
    "ativo": true
  }'
```

### 2. Listar Filmes Ativos

```bash
curl -X GET http://localhost:8082/filmes/ativos
```

### 3. Criar uma Sessão

```bash
curl -X POST http://localhost:8082/sessoes \
  -H "Content-Type: application/json" \
  -d '{
    "filmeId": 1,
    "salaId": 1,
    "horario": "2026-06-20T19:00:00",
    "preco": 35.00
  }'
```

### 4. Criar uma Reserva

```bash
curl -X POST http://localhost:8082/reservas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_JWT>" \
  -d '{
    "sessaoId": 1,
    "assentos": ["F12", "F13"]
  }'
```

### 5. Pagar uma Reserva

```bash
curl -X POST http://localhost:8082/reservas/1/pagar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_JWT>" \
  -d '{
    "metodoPagamento": "PIX"
  }'
```

---

## Integração e Eventos

Este microsserviço é o núcleo de transações de negócio do cinema.

* **Validação de Usuários**: Consulta o `usuarios-service` ou valida os tokens JWT para associar reservas a clientes.
* **Processamento de Pagamento**: Emite a solicitação de cobrança via API síncrona para o `pagamento-service`.
* **Mensageria**: Quando uma reserva é paga com sucesso, um evento `ReservaConfirmadaEvent` é publicado no tópico Kafka. Outros serviços, como o `Notification Service`, podem consumir este evento para enviar e-mails aos clientes.

---

## Diagrama de Arquitetura

```text
+---------------+
|    Cliente    |
+-------+-------+
        |
+-------v-------+
|  API Gateway  |
+-------+-------+
        |
+-------v-------+       (REST)          +-------------------+
| Eventos-Service|<-------------------> | Usuarios-Service  |
+-------+-------+                       +-------------------+
        |
        | (REST)
+-------v-------+                       +-------------------+
|Pagamento-Serv |  (Kafka Topic)        | Notification-Serv |
+---------------+ --------------------> |                   |
        |                               +-------------------+
+-------v-------+
| PostgreSQL DB |
| (eventos_db)  |
+---------------+
```

---

## Possíveis Melhorias Futuras

* Implementar cache (Redis) para a lista de filmes ativos e sessões disponíveis.
* Lock otimista/pessimista (JPA) para evitar concorrência na escolha de assentos de uma sessão.
* Documentação OpenAPI/Swagger.
* Testes de integração com Testcontainers (PostgreSQL e Kafka).

---

## Licença

Este projeto é parte do ecossistema Cineleo. Uso interno.
