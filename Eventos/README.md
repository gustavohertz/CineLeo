# 🎬 CineLeo Eventos Service

Microsserviço responsável pela gestão de **filmes**, **salas**, **sessões** e **reservas de ingressos** no ecossistema **CineLeo**.

Desenvolvido com **Spring Boot 3**, **Spring Cloud Eureka Client** e **PostgreSQL**, este serviço expõe uma API REST e se integra aos microsserviços de **Usuários** e **Pagamento** para validação de clientes e processamento de pagamentos, além de publicar eventos através do **Apache Kafka**.

---

## 📋 Visão Geral

O serviço Eventos é responsável por:

- Gerenciar filmes em cartaz;
- Gerenciar salas de cinema;
- Gerenciar sessões disponíveis;
- Realizar reservas de ingressos;
- Processar solicitações de pagamento;
- Publicar eventos de pagamento via Kafka;
- Integrar-se ao ecossistema CineLeo através do Eureka Server.

---

## 🏗 Arquitetura

O projeto segue uma arquitetura em camadas para facilitar manutenção, escalabilidade e testes.

### Controllers
Responsáveis por expor os endpoints REST da aplicação.

- FilmeController
- SalaController
- SessaoController
- ReservaController
- HealthCheckController

### Services
Contêm as regras de negócio da aplicação.

- Cadastro e gerenciamento de filmes;
- Controle de salas e sessões;
- Reserva de assentos;
- Validação de disponibilidade;
- Processamento de pagamentos;
- Integração com serviços externos.

### Repositories
Camada de acesso a dados utilizando Spring Data JPA.

- FilmeRepository
- SalaRepository
- SessaoRepository
- ReservaRepository

### Clients
Comunicação com outros microsserviços através de REST.

- UsuarioClient
- PagamentoClient

### Kafka Producer
Responsável pela publicação de eventos relacionados ao pagamento de reservas.

---

## 🔐 Segurança

Os endpoints protegidos exigem autenticação JWT.

### Header obrigatório

```http
Authorization: Bearer <token>
```

### Endpoints públicos

```text
/filmes/**
/salas/**
/sessoes/**
```

### Endpoints protegidos

```text
/reservas/**
```

---

## 📡 Endpoints Principais

| Método | Endpoint | Descrição |
|----------|----------|----------|
| GET | `/filmes/ativos` | Lista todos os filmes em cartaz |
| GET | `/sessoes/filme/{filmeId}` | Lista sessões disponíveis para um filme |
| POST | `/reservas` | Cria uma nova reserva |
| POST | `/reservas/{id}/pagar` | Processa o pagamento de uma reserva |
| GET | `/reservas/cliente?email={email}` | Lista reservas de um cliente |

---

## 🔄 Fluxo de Reserva

```text
Cliente
   │
   ▼
Consulta Filmes
   │
   ▼
Consulta Sessões
   │
   ▼
Seleciona Assentos
   │
   ▼
Cria Reserva
   │
   ▼
Valida Usuário
   │
   ▼
Solicita Pagamento
   │
   ▼
Pagamento Aprovado?
   │
 ┌─┴───────────┐
 │             │
 ▼             ▼
Sim           Não
 │             │
 ▼             ▼
Publica      Publica
Evento       Evento
Kafka        Kafka
 │             │
 ▼             ▼
Reserva      Reserva
Confirmada   Recusada
```

---

## ⚙️ Tecnologias Utilizadas

### Backend

- Java 21
- Spring Boot 3.4.0
- Spring Data JPA
- Spring Web
- Spring Validation

### Microsserviços

- Spring Cloud Netflix Eureka Client
- RestTemplate

### Mensageria

- Apache Kafka
- Spring Kafka

### Banco de Dados

- PostgreSQL 16

### Resiliência

- Resilience4j Circuit Breaker
- Resilience4j Retry

### Observabilidade

- Micrometer
- OpenTelemetry
- Prometheus
- Grafana Tempo

### Build

- Maven

---

## ▶️ Como Executar

### 1. Subir infraestrutura

Execute:

```bash
docker-compose up -d
```

---

### 2. Verificar dependências

Certifique-se de que os seguintes serviços estejam em execução:

| Serviço | Porta |
|----------|----------|
| Eureka Server | 8761 |
| Usuarios Service | 8083 |
| Pagamento Service | 5000 |
| Kafka | 9092 |
| PostgreSQL | 5432 |

---

### 3. Executar aplicação

```bash
mvn spring-boot:run
```

---

### 4. Acessar API

```text
http://localhost:8082
```

---

## 🔗 Integração com o Cliente

A interação dos usuários finais ocorre através do projeto:

```text
/App
```

O cliente consome a API por meio do:

```text
http://localhost:9999
```

(API Gateway)

O microsserviço Eventos não possui interface própria, sendo exclusivamente uma API REST.

---

## 📊 Observabilidade

### Tracing Distribuído

OpenTelemetry configurado para exportação OTLP.

Exemplo:

```properties
management.otlp.tracing.endpoint=http://localhost:4318/v1/traces
```

### Métricas

Disponíveis em:

```text
/actuator/prometheus
```

### Logs

Logs correlacionados utilizando:

```text
traceId
spanId
```

Facilitando rastreamento entre microsserviços.

---

## 📁 Estrutura do Projeto

```text
Eventos/
├── docker-compose.yml
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── cineleo/
    │   │           └── eventos/
    │   │               ├── EventosApplication.java
    │   │
    │   │               ├── client/
    │   │               │   ├── PagamentoClient.java
    │   │               │   └── UsuarioClient.java
    │   │
    │   │               ├── config/
    │   │               │   ├── RestConfig.java
    │   │               │   └── SecurityConfig.java
    │   │
    │   │               ├── controller/
    │   │               │   ├── FilmeController.java
    │   │               │   ├── HealthCheckController.java
    │   │               │   ├── ReservaController.java
    │   │               │   ├── SalaController.java
    │   │               │   └── SessaoController.java
    │   │
    │   │               ├── dto/
    │   │               │   ├── EnderecoDTO.java
    │   │               │   ├── FilmeRequestDTO.java
    │   │               │   ├── FilmeResponseDTO.java
    │   │               │   ├── PagamentoEvento.java
    │   │               │   ├── PagamentoReservaRequestDTO.java
    │   │               │   ├── ReservaRequestDTO.java
    │   │               │   ├── ReservaResponseDTO.java
    │   │               │   ├── SalaRequestDTO.java
    │   │               │   ├── SalaResponseDTO.java
    │   │               │   ├── SessaoRequestDTO.java
    │   │               │   └── SessaoResponseDTO.java
    │   │
    │   │               ├── entity/
    │   │               │   ├── Endereco.java
    │   │               │   ├── Filme.java
    │   │               │   ├── Reserva.java
    │   │               │   ├── Sala.java
    │   │               │   └── Sessao.java
    │   │
    │   │               ├── exception/
    │   │               │   ├── BusinessException.java
    │   │               │   ├── ConflictException.java
    │   │               │   ├── GlobalExceptionHandler.java
    │   │               │   └── ResourceNotFoundException.java
    │   │
    │   │               ├── repository/
    │   │               │   ├── FilmeRepository.java
    │   │               │   ├── ReservaRepository.java
    │   │               │   ├── SalaRepository.java
    │   │               │   └── SessaoRepository.java
    │   │
    │   │               └── service/
    │   │                   ├── FilmeService.java
    │   │                   ├── ReservaService.java
    │   │                   ├── SalaService.java
    │   │                   └── SessaoService.java
    │
    │   └── resources/
    │       ├── application.properties
    │       ├── data.sql
    │       └── logback-spring.xml
    │
    └── test/
        └── java/
            └── com/
                └── cineleo/
                    └── eventos/
                        ├── EventosApplicationTests.java
                        └── service/
                            ├── FilmeServiceTest.java
                            └── SalaServiceTest.java
```

---

## 🧪 Testes

Para executar os testes:

```bash
mvn test
```

Para gerar o pacote da aplicação:

```bash
mvn clean package
```

---

## ✅ Refatoração Realizada

As seguintes alterações foram realizadas para adequação da arquitetura de microsserviços:

1. Remoção da pasta:

```text
cli/
```

2. Remoção da classe:

```text
JwtUtil.java
```

3. Separação completa da interface terminal para o projeto:

```text
App
```

4. Manutenção do serviço Eventos exclusivamente como backend REST.

---

## 📄 Licença

Projeto de uso interno do ecossistema **CineLeo**.

Todos os direitos reservados.