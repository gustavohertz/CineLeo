# Notification & Messaging Service - CineLeo

Microsserviço responsável pelo gerenciamento de notificações e orquestração de envio de e-mails do ecossistema **CineLeo**.

Desenvolvido com **Java 21**, **Spring Boot 3.5.3**, **PostgreSQL** e **Apache Kafka**, este serviço recebe notificações via REST, realiza persistência em banco de dados e coordena o envio assíncrono de e-mails com confirmação de entrega.

---

## Sumário

* [Visão Geral](#visão-geral)
* [Arquitetura](#arquitetura)
* [Fluxo de Envio de E-mail](#fluxo-de-envio-de-e-mail)
* [Estrutura do Projeto](#estrutura-do-projeto)
* [Tecnologias Utilizadas](#tecnologias-utilizadas)
* [Pré-requisitos](#pré-requisitos)
* [Configuração](#configuração)
* [Banco de Dados](#banco-de-dados)
* [Execução](#execução)
* [Endpoints Disponíveis](#endpoints-disponíveis)
* [Tratamento de Erros](#tratamento-de-erros)
* [Testes](#testes)
* [Observabilidade](#observabilidade)
* [Segurança e Boas Práticas](#segurança-e-boas-práticas)
* [Melhorias Futuras](#melhorias-futuras)

---

## Visão Geral

O **Notification Service** é responsável por centralizar o gerenciamento de notificações dentro da arquitetura de microsserviços do CineLeo.

### Principais responsabilidades

- Receber notificações via REST
- Persistir notificações em PostgreSQL
- Consultar notificações por ID
- Publicar solicitações de envio no Kafka
- Receber confirmação de entrega
- Garantir idempotência no envio
- Gerenciar cache em memória
- Enviar logs de requisições HTTP para o serviço de Observabilidade

---

## Arquitetura

```
                    Cliente / Microsserviços
                               │
                               ▼
                  ┌─────────────────────────┐
                  │ Notification Controller  │
                  └─────────────┬───────────┘
                                │
                                ▼
                  ┌─────────────────────────┐
                  │  Notification Service   │
                  └─────────────┬───────────┘
                                │
         ┌──────────────────────┼──────────────────────┐
         │                      │                      │
         ▼                      ▼                      ▼
    PostgreSQL            Cache Local          Kafka Producer
                                                     │
                                                     ▼
                                        notification.email.send
                                                     │
                                                     ▼
                                          Microservices Kafka
                                                     │
                                                     ▼
                                        notification.email.sent
                                                     │
                                                     ▼
                                          Kafka Consumer
```

---

## Fluxo de Envio de E-mail

```
Cliente
   │
   ▼
POST /notification/send-email/{id}
   │
   ▼
Notification Service
   │
   ▼
Valida notificação
   │
   ▼
Publica evento Kafka (notification.email.send)
   │
   ▼
Microservices Kafka → SMTP / JavaMailSender
   │
   ▼
notification.email.sent
   │
   ▼
Notification Service
   ├── SENT    → Atualiza banco → Retorna 200
   └── FAILED  → Retorna 502
```

---

## Estrutura do Projeto

```
Notification/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/leocine/
    │   │   ├── notificationApp.java
    │   │   ├── controller/
    │   │   │   ├── NotificationController.java
    │   │   │   └── HealthCheckController.java
    │   │   ├── service/
    │   │   │   └── NotificationService.java
    │   │   ├── repository/
    │   │   │   └── NotificationJpaRepository.java
    │   │   ├── entity/
    │   │   │   └── NotificationMessage.java
    │   │   ├── dto/
    │   │   │   ├── NotificationRequestDTO.java
    │   │   │   ├── NotificationResponseDTO.java
    │   │   │   └── ConsumeResponseDTO.java
    │   │   ├── exception/
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   └── NotificationProcessingException.java
    │   │   └── interceptor/
    │   │       ├── RequestLoggingInterceptor.java
    │   │       ├── LogEventDTO.java
    │   │       └── WebMvcConfig.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/leocine/service/
            └── NotificationServiceTest.java
```

---

## Tecnologias Utilizadas

| Tecnologia         | Versão |
|--------------------|--------|
| Java               | 21     |
| Spring Boot        | 3.5.3  |
| Spring Cloud       | 2025.0.0 |
| Spring Data JPA    | Latest |
| Spring Kafka       | Latest |
| PostgreSQL         | 16     |
| Lombok             | Latest |
| Bean Validation    | Latest |
| JUnit 5 + Mockito  | Latest |
| Eureka Client      | Latest |

---

## Pré-requisitos

* JDK 21
* Maven 3.8+
* PostgreSQL 16+
* Apache Kafka
* Eureka Server

Infraestrutura padrão:

| Serviço              | Porta |
|----------------------|-------|
| Notification Service | 8000  |
| PostgreSQL           | 5432  |
| Kafka                | 9092  |
| Eureka Server        | 8761  |
| Observabilidade      | 8090  |

---

## Configuração

### application.properties

```properties
spring.application.name=notification-service
server.port=8000

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.kafka.producer.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
```

---

## Banco de Dados

### Configuração padrão

| Parâmetro | Valor    |
|-----------|----------|
| Banco     | postgres |
| Usuário   | postgres |
| Senha     | root     |
| Porta     | 5432     |

---

## Execução

```bash
mvn spring-boot:run
```

API disponível em: `http://localhost:8000`

---

## Endpoints Disponíveis

### Health Check

```http
GET /health-check
```

```json
{
  "success": "ok"
}
```

### Criar Notificação

```http
POST /notification/consume
Content-Type: application/json
```

```json
{
  "userID": "10",
  "userEmail": "user@email.com",
  "msgString": "Mensagem",
  "dateTime": "2026-06-16T10:00:00Z"
}
```

### Consultar Notificação

```http
GET /notification/{id}
```

### Enviar E-mail

```http
POST /notification/send-email/{id}
```

---

## Exemplos cURL

```bash
# Health Check
curl -X GET http://localhost:8000/health-check

# Criar Notificação
curl -X POST http://localhost:8000/notification/consume \
  -H "Content-Type: application/json" \
  -d '{"userID":"10","userEmail":"user@email.com","msgString":"Mensagem","dateTime":"2026-06-16T10:00:00Z"}'

# Buscar Notificação
curl -X GET http://localhost:8000/notification/{id}

# Enviar E-mail
curl -X POST http://localhost:8000/notification/send-email/{id}
```

---

## Tratamento de Erros

| HTTP | Situação                   |
|------|----------------------------|
| 400  | Dados inválidos            |
| 404  | Notificação não encontrada |
| 409  | E-mail já enviado          |
| 502  | Falha Kafka ou SMTP        |
| 500  | Erro interno               |

Formato padrão:

```json
{
  "timestamp": "2026-06-22T12:44:18",
  "status": 502,
  "error": "Bad Gateway",
  "message": "Descrição detalhada"
}
```

---

## Testes

```bash
mvn test
```

### Cenários cobertos (NotificationServiceTest)

| Teste | Descrição |
|-------|-----------|
| `deveCriarNotificacao` | Cria notificação e persiste no banco |
| `deveBuscarNotificacaoPorId` | Busca notificação existente por ID |
| `deveLancarExcecaoQuandoNotificacaoNaoEncontrada` | Retorna erro para ID inexistente |
| `deveLancarExcecaoQuandoIdNuloNoBuscar` | Retorna erro para ID nulo |
| `deveEnviarEmailComSucesso` | Publica no Kafka e aguarda confirmação de envio |
| `deveLancarExcecaoAoEnviarEmailComIdNulo` | Retorna erro ao tentar enviar com ID nulo |

### Dependências de teste

- **JUnit 5** (via `spring-boot-starter-test`)
- **Mockito** para mock de `NotificationJpaRepository` e `KafkaTemplate`

---

## Observabilidade

O serviço possui um **interceptor de requisições HTTP** (`RequestLoggingInterceptor`) que envia automaticamente logs de todas as requisições para o serviço de Observabilidade.

- Requisições com status `>= 400` são registradas como `ERROR`
- Requisições com status `< 400` são registradas como `INFO`
- Exceções não tratadas são capturadas e logadas como `ERROR`
- Endpoint de destino: `POST http://localhost:8090/observabilidade/logs`
- Endpoints excluídos: `/actuator/**` e `/health-check`

---

## Segurança e Boas Práticas

* IDs gerados exclusivamente pelo backend (UUID)
* Bean Validation em todas as entradas
* Tratamento centralizado de exceções via `GlobalExceptionHandler`
* Cache somente após persistência bem-sucedida
* Idempotência garantida para envios
* Confirmação real antes de retornar sucesso
* Persistência do status de envio

---

## Melhorias Futuras

* JWT Authentication
* OpenAPI / Swagger
* Templates HTML para e-mails
* Retry automático com backoff
* Dead Letter Queue (DLQ)
* Prometheus + Micrometer
* Dashboard administrativo
* Histórico completo de entregas

---

Projeto desenvolvido para fins acadêmicos e educacionais como parte do ecossistema **CineLeo**.
