# 📬 Notification & Messaging Service

Microsserviço de notificações e mensageria desenvolvido em **Java 21**, **Spring Boot 3** e **Maven**.

Responsável por receber (via REST) notificações/mensagens, persisti-las em **PostgreSQL** e permitir envio de e-mail (simulado) e consulta por **ID**.

A aplicação mantém um **cache em memória** para consultas rápidas e garante consistência entre cache e banco de dados. O envio de e-mail é **idempotente**, com controle persistido.

---

# 📑 Sumário

* [Tecnologias](#-tecnologias)
* [Arquitetura](#-arquitetura-camadas)
* [Configuração](#-configuração-porta-e-banco)
* [PostgreSQL com Docker](#-subindo-o-postgresql-com-docker-recomendado)
* [Execução](#-executando-o-projeto)
* [Endpoints](#-endpoints)

  * [Health Check](#1-health-check)
  * [Consumir Notificação](#2-consumiringestar-notificação-http)
  * [Consultar por ID](#3-consultar-notificação-por-id)
  * [Enviar E-mail](#4-enviar-e-mail-por-id-simulação-idempotente)
* [Exemplos cURL](#-exemplos-curl-windows-com-)
* [Testes](#-testes)
* [Build para Produção](#-build-para-produção)
* [Segurança e Boas Práticas](#-segurança-e-boas-práticas-após-refatoração)
* [Licença](#-license)

---

# 🚀 Tecnologias

* Java 21
* Spring Boot 3
* Spring Data JPA
* PostgreSQL
* Maven
* REST API / JSON
* Bean Validation (`jakarta.validation`)
* Docker (PostgreSQL via `docker-compose.yml`)
* JUnit (testes)

---

# 🏗️ Arquitetura (camadas)

```text
Controller
   │
   ├── Trata requisições HTTP
   └── Delega processamento

DTO
   │
   ├── @Valid
   ├── @NotBlank
   └── @Email

Service
   │
   ├── Regras de negócio
   ├── Cache em memória
   └── Orquestração

Repository
   │
   └── Spring Data JPA

Database
   │
   └── PostgreSQL
```

### Tratamento de erros

Os erros são tratados de forma centralizada através de um:

```java
GlobalExceptionHandler
```

Retornando códigos HTTP apropriados:

| Código | Significado           |
| ------ | --------------------- |
| 400    | Bad Request           |
| 404    | Not Found             |
| 409    | Conflict              |
| 500    | Internal Server Error |

---

# ⚙️ Configuração (porta e banco)

Arquivo:

```text
Notification/src/main/resources/application.properties
```

### Porta da aplicação

```properties
server.port=8000
```

### PostgreSQL

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres

spring.datasource.username=postgres

spring.datasource.password=root
```

### Hibernate

```properties
spring.jpa.hibernate.ddl-auto=update
```

Mantém o schema sincronizado com as entidades.

---

# 🐘 Subindo o PostgreSQL com Docker (recomendado)

Na pasta `Notification/` execute:

```bash
docker compose up -d
```

O `docker-compose.yml` cria um container com:

| Configuração | Valor     |
| ------------ | --------- |
| Banco        | postgres  |
| Usuário      | postgres  |
| Senha        | root      |
| Porta        | 5432:5432 |

---

# ▶️ Executando o projeto

Na pasta `Notification/`:

### Compilar

```bash
mvn clean install
```

### Rodar

```bash
mvn spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8000
```

---

# 📌 Endpoints

## 1. Health Check

### Request

```http
GET /health-check
```

### Response (200)

```json
{
  "success": "ok"
}
```

### Response (503)

Caso a porta do servidor seja inválida:

```json
{
  "success": "error",
  "message": "Invalid server port: X"
}
```

---

## 2. Consumir/ingestar notificação (HTTP)

Cria uma nova notificação com ID gerado automaticamente pelo backend.

### Request

```http
POST /notification/consume
Content-Type: application/json
```

### Body

```json
{
  "userID": "10",
  "userEmail": "user@email.com",
  "msgString": "Mensagem",
  "dateTime": "2026-06-16T10:00:00Z"
}
```

> Todos os campos são obrigatórios, exceto `dateTime`.

### Response (201 Created)

```json
{
  "id": "uuid-gerado-pelo-backend",
  "status": "ok"
}
```

### Erro 400 - Bad Request

Campos inválidos ou ausentes.

```json
{
  "timestamp": "2026-06-17T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "userEmail": "Invalid email format"
  }
}
```

### Erro 500 - Internal Server Error

```json
{
  "timestamp": "...",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

---

## 3. Consultar notificação por ID

### Request

```http
GET /notification/{id}
```

### Response (200)

```json
{
  "id": "uuid-gerado",
  "userID": "10",
  "userEmail": "user@email.com",
  "msgString": "Mensagem",
  "dateTime": "2026-06-16T10:00:00Z"
}
```

### Erro 404 - Not Found

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "message": "Notification not found for ID: xyz"
}
```

---

## 4. Enviar e-mail por ID (simulação idempotente)

Simula o envio de e-mail.

O sistema garante que um mesmo ID tenha o e-mail enviado apenas uma vez, persistindo o momento do envio.

### Request

```http
POST /notification/send-email/{id}
```

### Response (200 OK)

```json
"Email sent successfully"
```

### Erro 409 - Conflict

E-mail já enviado anteriormente.

```json
{
  "timestamp": "...",
  "status": 409,
  "error": "Conflict",
  "message": "E-mail already sent for notification id: xyz"
}
```

### Erro 404 - Not Found

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "message": "Notification not found for ID: xyz"
}
```

---

# 🧾 Exemplos cURL (Windows, com ^)

## Health Check

```bash
curl -X GET http://localhost:8000/health-check
```

---

## Consumir Notificação

```bash
curl -X POST http://localhost:8000/notification/consume ^
  -H "Content-Type: application/json" ^
  -d "{\"userID\":\"10\",\"userEmail\":\"user@email.com\",\"msgString\":\"Mensagem\",\"dateTime\":\"2026-06-16T10:00:00Z\"}"
```

---

## Buscar por ID

```bash
curl -X GET http://localhost:8000/notification/{id}
```

---

## Enviar E-mail

```bash
curl -X POST http://localhost:8000/notification/send-email/{id}
```

---

# 🧪 Testes

Executar todos os testes:

```bash
mvn test
```

---

# 📦 Build para Produção

Gerar o artefato:

```bash
mvn clean package
```

### Saída

```text
target/notification-1.0.jar
```

### Executar

```bash
java -jar target/notification-1.0.jar
```

---

# 🔒 Segurança e Boas Práticas (após refatoração)

## ID gerado pelo backend

O endpoint de criação não aceita ID externo, prevenindo manipulação maliciosa.

---

## Validação de entrada

Utilização de:

* `@Valid`
* `@NotBlank`
* `@Email`

Retornando erros detalhados com HTTP 400.

---

## Tratamento centralizado de exceções

Implementado através de:

```java
@RestControllerAdvice
```

Mapeando exceções para:

* 400 Bad Request
* 404 Not Found
* 409 Conflict
* 500 Internal Server Error

Sem exposição de stack traces.

---

## Idempotência no envio de e-mails

O campo:

```java
sentAt
```

é persistido no banco.

Mesmo após reinicialização da aplicação, reenvios são bloqueados.

---

## Cache em memória consistente

Os dados são adicionados ao cache apenas após persistência bem-sucedida no banco de dados.

---

# 🚀 Evoluções Futuras

* Adicionar autenticação/autorização via JWT.
* Implementar envio real de e-mails.
* Integração com SMTP.
* Integração com filas (Kafka/RabbitMQ).
* Rate Limiting.
* Sanitização de payloads.
* Logs de auditoria estruturados.
* Observabilidade com Prometheus e Micrometer.
* OpenAPI / Swagger.

---

# 📄 License

Projeto educacional e de demonstração de arquitetura de microsserviços utilizando:

* Java 21
* Spring Boot 3
* Spring Data JPA
* PostgreSQL

Desenvolvido para estudo e evolução de arquiteturas baseadas em microsserviços.
