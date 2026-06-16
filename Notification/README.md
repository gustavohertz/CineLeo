# 📬 Notification & Messaging Service

Microsserviço de notificações e mensageria desenvolvido em **Java 21**, **Spring Boot 3** e **Maven**. Responsável por receber (via REST) notificações/mensagens, persistir em **PostgreSQL** e permitir consumo/consulta por **ID**.

> Observação: a aplicação mantém também um **cache em memória** (`memoryStore`) para consulta imediata. A persistência ocorre via JPA/EntityManager (`NotificationRepository`).

---

## 🚀 Tecnologias

- Java 21
- Spring Boot 3
- Maven
- REST API
- JSON
- JPA (PostgreSQL)
- JUnit (testes)
- Docker (PostgreSQL via `docker-compose.yml`)

---

## 🏗️ Arquitetura (camadas)

```text
Controller
   ↓
DTO
   ↓
Service
   ↓
Repository
   ↓
Database (PostgreSQL)
```

---

## ⚙️ Configuração (porta e banco)

A API roda na porta definida em:

- `Notification/src/main/resources/application.properties`
  - `server.port=8000`
  - PostgreSQL:
    - `spring.datasource.url=jdbc:postgresql://localhost:5432/postgres`
    - `spring.datasource.username=postgres`
    - `spring.datasource.password=root`

> Em `spring.jpa.hibernate.ddl-auto=update`, o schema é atualizado automaticamente com base nas entidades.

---

## 🐘 Subindo o PostgreSQL com Docker (recomendado)

1. Na pasta `Notification/`, suba o banco com:

```bash
docker compose up -d
```

2. O `docker-compose.yml` cria um container com:
- DB: `postgres`
- user: `postgres`
- password: `root`
- porta: `5432:5432`

---

## ▶️ Executando o projeto

Na pasta `Notification/`:

### Compilar
```bash
mvn clean install
```

### Rodar
```bash
mvn spring-boot:run
```

A API estará disponível em:
- http://localhost:8000

---

## 📌 Endpoints

### 1) Health Check

**Request**
```http
GET /health-check
```

**Response (200)**
```json
{
  "success": "ok"
}
```

**Response (503)**
Quando a porta do servidor estiver inválida (<= 0):
```json
{
  "success": "error",
  "message": "Invalid server port: X"
}
```

---

### 2) Consumir/ingestar notificação (HTTP)

Este endpoint **recebe** um payload e:
- valida `id` (obrigatório)
- salva em `memoryStore`
- tenta persistir via `NotificationRepository`
- retorna status `ok` ou `error` sem stacktrace para o cliente

**Request**
```http
POST /notification/consume
Content-Type: application/json
```

#### Body (JSON)
> Campos:
- `id` (String, obrigatório)
- `userID` (String)
- `userEmail` (String)
- `msgString` (String)
- `dateTime` (OffsetDateTime opcional; se vier `null`, o service usa `now()`)

Exemplo:
```json
{
  "id": "1",
  "userID": "10",
  "userEmail": "user@email.com",
  "msgString": "Mensagem",
  "dateTime": "2026-06-16T10:00:00Z"
}
```

**Response (200)**
```json
{
  "id": "1",
  "status": "ok"
}
```

Em caso de erro:
```json
{
  "id": "1",
  "status": "error"
}
```

---

### 3) Consultar notificação por ID

**Request**
```http
GET /notification/{id}
```

**Response (200)**
```json
{
  "id": "1",
  "userID": "10",
  "userEmail": "user@email.com",
  "msgString": "Mensagem",
  "dateTime": "2026-06-16T10:00:00Z"
}
```

> Caso `id` seja inválido/vazio ou a notificação não exista, o service lança `NotificationProcessingException`.

---

### 4) Enviar e-mail por ID (stub)

Este endpoint executa uma regra de negócio: **enviar e-mail apenas 1 vez por ID**.
Por enquanto, a integração real de e-mail não está implementada; a resposta indica sucesso/falha conforme a regra.

**Request**
```http
POST /notification/send-email/{id}
```

**Response (200)**
```json
"ok"
```

Se tentar reenviar para o mesmo `id`, a aplicação lança exceção (tratamento via exception handler, se existir no projeto).

---

## 🧾 Exemplos cURL

### Health Check
```bash
curl -X GET http://localhost:8000/health-check
```

### Consume (POST)
```bash
curl -X POST http://localhost:8000/notification/consume ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"1\",\"userID\":\"10\",\"userEmail\":\"user@email.com\",\"msgString\":\"Mensagem\",\"dateTime\":\"2026-06-16T10:00:00Z\"}"
```

### Buscar por ID
```bash
curl -X GET http://localhost:8000/notification/1
```

### Enviar e-mail por ID
```bash
curl -X POST http://localhost:8000/notification/send-email/1
```

---

## 🧪 Testes

```bash
mvn test
```

---

## 📦 Build para Produção

```bash
mvn clean package
```

Saída:
- `target/notification-1.0.jar` (arquivo gerado pelo build)

Executar:
```bash
java -jar target/notification-1.0.jar
```

---

## 🔒 Segurança e Boas Práticas (implementação atual)

- O service valida `id` (obrigatório).
- Tratamento de exceções é feito via `NotificationProcessingException` (e controller captura exceções em `/notification/consume` devolvendo status `error`).
- Recomendado para evolução futura:
  - validações mais completas (ex.: e-mail formatado, tamanho de msg)
  - autenticação/autorização (ex.: JWT)
  - sanitização/limites de payload
  - logs de auditoria e correlação de requisições

---

## 📄 License

Projeto educacional/demonstração de arquitetura de microsserviços usando Java 21, Spring Boot e Maven.
