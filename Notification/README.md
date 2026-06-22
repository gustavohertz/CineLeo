# 📬 Notification & Messaging Service - CineLeo

Microsserviço responsável pelo gerenciamento de notificações e orquestração de envio de e-mails do ecossistema **CineLeo**.

Desenvolvido com **Java 21**, **Spring Boot 3**, **PostgreSQL** e **Apache Kafka**, este serviço recebe notificações via REST, realiza persistência em banco de dados e coordena o envio assíncrono de e-mails com confirmação de entrega.

---

# 📋 Sumário

* [Visão Geral](#-visão-geral)
* [Arquitetura](#-arquitetura)
* [Fluxo de Envio de E-mail](#-fluxo-de-envio-de-e-mail)
* [Estrutura do Projeto](#-estrutura-do-projeto)
* [Tecnologias Utilizadas](#-tecnologias-utilizadas)
* [Pré-requisitos](#-pré-requisitos)
* [Configuração](#-configuração)
* [Banco de Dados](#-banco-de-dados)
* [Execução](#-execução)
* [Endpoints Disponíveis](#-endpoints-disponíveis)
* [Tratamento de Erros](#-tratamento-de-erros)
* [Build e Containerização](#-build-e-containerização)
* [Testes](#-testes)
* [Segurança e Boas Práticas](#-segurança-e-boas-práticas)
* [Melhorias Futuras](#-melhorias-futuras)
* [Licença](#-licença)

---

# 🔍 Visão Geral

O **Notification Service** é responsável por centralizar o gerenciamento de notificações dentro da arquitetura de microsserviços do CineLeo.

### Principais responsabilidades

✅ Receber notificações via REST

✅ Persistir notificações em PostgreSQL

✅ Consultar notificações por ID

✅ Publicar solicitações de envio no Kafka

✅ Receber confirmação de entrega

✅ Garantir idempotência no envio

✅ Gerenciar cache em memória

✅ Centralizar regras de negócio relacionadas a notificações

---

# 🏗 Arquitetura

```text id="v9g5mk"
                    Cliente / Microsserviços
                               │
                               ▼

                  ┌─────────────────────────┐
                  │ Notification Controller │
                  └─────────────┬───────────┘
                                │
                                ▼

                  ┌─────────────────────────┐
                  │ Notification Service    │
                  └─────────────┬───────────┘
                                │

         ┌──────────────────────┼──────────────────────┐
         │                      │                      │
         ▼                      ▼                      ▼

 PostgreSQL              Cache Local          Kafka Producer
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

# 🔄 Fluxo de Envio de E-mail

```text id="a4s0df"
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

Publica evento Kafka
(notification.email.send)
   │
   ▼

Microservices Kafka
   │
   ▼

SMTP / JavaMailSender
   │
   ▼

notification.email.sent
   │
   ▼

Notification Service
   │
   ├── SENT
   │      ▼
   │   Atualiza banco
   │   Retorna 200
   │
   └── FAILED
          ▼
      Retorna 502
```

---

# 📁 Estrutura do Projeto

```text id="f0b8sq"
src/
└── main/
    ├── java/
    │   └── com/cineleo/notification/
    │
    ├── controller/
    │   ├── NotificationController.java
    │   └── HealthCheckController.java
    │
    ├── service/
    │   └── NotificationService.java
    │
    ├── repository/
    │   └── NotificationRepository.java
    │
    ├── consumer/
    │   └── EmailStatusConsumer.java
    │
    ├── producer/
    │   └── EmailProducer.java
    │
    ├── dto/
    │   ├── NotificationRequestDTO.java
    │   ├── NotificationResponseDTO.java
    │   └── EmailRequestDTO.java
    │
    └── entity/
        └── Notification.java

src/main/resources/
└── application.properties

pom.xml
Dockerfile
```

---

# 🚀 Tecnologias Utilizadas

| Tecnologia      | Versão   |
| --------------- | -------- |
| Java            | 21       |
| Spring Boot     | 3.5.3    |
| Spring Data JPA | Latest   |
| Spring Kafka    | Latest   |
| PostgreSQL      | 16       |
| Lombok          | Latest   |
| Bean Validation | Latest   |
| JUnit 5         | Latest   |
| Mockito         | Latest   |
| Docker          | Opcional |

---

# 📋 Pré-requisitos

* JDK 21
* Maven 3.8+
* PostgreSQL 16+
* Apache Kafka
* Eureka Server
* Docker (opcional)

Infraestrutura padrão:

| Serviço              | Porta |
| -------------------- | ----- |
| Notification Service | 8000  |
| PostgreSQL           | 5432  |
| Kafka                | 9092  |
| Eureka Server        | 8761  |

---

# ⚙️ Configuração

## application.properties

```properties id="mrny97"
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

# 🐘 Banco de Dados

## Subir PostgreSQL

```bash id="g9cnwd"
docker compose up -d
```

### Configuração padrão

| Parâmetro | Valor    |
| --------- | -------- |
| Banco     | postgres |
| Usuário   | postgres |
| Senha     | root     |
| Porta     | 5432     |

---

# ▶️ Execução

## Executar Aplicação

```bash id="mxwe7r"
mvn spring-boot:run
```

ou execute a classe:

```text id="z48hbd"
NotificationApplication
```

API disponível em:

```text id="rvzqmv"
http://localhost:8000
```

---

# 🌐 Endpoints Disponíveis

## Health Check

### Request

```http id="ib2t4m"
GET /health-check
```

### Response

```json id="7a9yiu"
{
  "success": "ok"
}
```

---

## Criar Notificação

### Request

```http id="9x4q4v"
POST /notification/consume
Content-Type: application/json
```

### Body

```json id="i0m83m"
{
  "userID": "10",
  "userEmail": "user@email.com",
  "msgString": "Mensagem",
  "dateTime": "2026-06-16T10:00:00Z"
}
```

### Response

```json id="0vjqsl"
{
  "id": "uuid-gerado",
  "status": "ok"
}
```

---

## Consultar Notificação

### Request

```http id="wb4hzr"
GET /notification/{id}
```

### Response

```json id="3wwkjm"
{
  "id": "abc-123",
  "userID": "10",
  "userEmail": "user@email.com"
}
```

---

## Enviar E-mail

### Request

```http id="n7ihgo"
POST /notification/send-email/{id}
```

### Sucesso

```json id="ghm0wt"
"Email sent successfully"
```

### Falha SMTP

```json id="fgl3k6"
{
  "status": 502,
  "message": "Failed to send email: Invalid SMTP credentials"
}
```

---

# 🧪 Exemplos cURL

## Health Check

```bash id="h25xv9"
curl -X GET http://localhost:8000/health-check
```

---

## Criar Notificação

```bash id="oz2jcb"
curl -X POST http://localhost:8000/notification/consume \
-H "Content-Type: application/json" \
-d '{
  "userID":"10",
  "userEmail":"user@email.com",
  "msgString":"Mensagem",
  "dateTime":"2026-06-16T10:00:00Z"
}'
```

---

## Buscar Notificação

```bash id="up9hdk"
curl -X GET http://localhost:8000/notification/{id}
```

---

## Enviar E-mail

```bash id="e6yh9i"
curl -X POST http://localhost:8000/notification/send-email/{id}
```

---

# 🛡 Tratamento de Erros

| HTTP | Situação                   |
| ---- | -------------------------- |
| 400  | Dados inválidos            |
| 404  | Notificação não encontrada |
| 409  | E-mail já enviado          |
| 502  | Falha Kafka ou SMTP        |
| 500  | Erro interno               |

Formato padrão:

```json id="5ow4gr"
{
  "timestamp": "2026-06-22T12:44:18",
  "status": 502,
  "error": "Bad Gateway",
  "message": "Descrição detalhada"
}
```

---

# 🐳 Build e Containerização

## Gerar JAR

```bash id="0h1vdf"
mvn clean package
```

---

## Executar JAR

```bash id="v2ajkv"
java -jar target/notification-service.jar
```

---

## Construir Docker

```bash id="o5l5ic"
docker build -t notification-service .
```

---

## Executar Container

```bash id="vuhmh8"
docker run -p 8000:8000 notification-service
```

---

# ✅ Testes

Executar todos os testes:

```bash id="k4zplx"
mvn test
```

Principais cenários cobertos:

* Persistência de notificações
* Consulta por ID
* Publicação Kafka
* Consumo de confirmação
* Tratamento de erros
* Idempotência de envio

---

# 🔒 Segurança e Boas Práticas

* IDs gerados exclusivamente pelo backend
* Bean Validation em todas as entradas
* Tratamento centralizado de exceções
* Cache somente após persistência bem-sucedida
* Idempotência garantida para envios
* Confirmação real antes de retornar sucesso
* Persistência do status de envio

---

# 🔮 Melhorias Futuras

* JWT Authentication
* OpenAPI / Swagger
* Templates HTML
* Retry automático
* Dead Letter Queue (DLQ)
* Prometheus + Micrometer
* Dashboard administrativo
* Histórico completo de entregas

---

# 📄 Licença

Projeto desenvolvido para fins acadêmicos e educacionais como parte do ecossistema **CineLeo**.

---

## 👨‍💻 Desenvolvido para o Ecossistema CineLeo

Notifications • Kafka • PostgreSQL • Spring Boot • Event Driven Architecture • Java 21
