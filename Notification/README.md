# 📬 Notification & Messaging Service

Microsserviço de notificações e mensageria desenvolvido em **Java 21**, **Spring Boot 3** e **Maven**, responsável pelo envio e consumo de mensagens, notificações e comunicação assíncrona entre sistemas.

## 🚀 Tecnologias

* Java 21
* Spring Boot 3
* Maven
* REST API
* JSON
* JPA (PostgreSQL)
* REST API
* JSON
* JUnit
* Docker (opcional)


---

# 📋 Funcionalidades

### Mensageria

* Envio de mensagens para filas/tópicos
* Consumo assíncrono de mensagens
* Suporte a diferentes protocolos (HTTP, AMQP, etc.)
* Confirmação de entrega (ACK/NACK)

### Notificações

* Notificação por e-mail, SMS, push (a definir)
* Agendamento de notificações
* Priorização de canais

### Monitoramento

* Health check do serviço
* Criamento e Rastreamento de mensagens

---

# 🏗️ Arquitetura

O projeto segue a arquitetura em camadas:

```text
Controller
   ↓
DTO
   ↓
Service
   ↓
Repository
   ↓
DataBase
```

### Controller

Responsável pelas rotas HTTP (health check, envio de notificações).

### DTO

Responsável pela transferência de dados entre camadas.

### Service

Responsável pelas regras de negócio e integração com o broker de mensageria.

### Repository

Pode ser utilizado para persistência de logs/status de notificações.

### DataBase

Persistência em **PostgreSQL** para armazenar as notificações (mensagens) pelo `id`.

---

# ⚙️ Executando o Projeto

## Clonar repositório

```bash
git clone https://github.com/seu-usuario/notification-service.git
```

```bash
cd Notification
```

## Compilar

```bash
mvn clean install
```

## Executar

```bash
mvn spring-boot:run
```

A aplicação será iniciada em:

```text
http://localhost:8000
```

---

# 📌 Endpoints e consumo de mensagens

## 1 - Health Check

### Request

```http
GET /health-check
```

---

## 2 - Consumir/ingestar mensagem (HTTP)

> Este Notification é independente: você pode enviar a mensagem via REST para persistir no PostgreSQL.

### Request

```http
POST /notification/consume
Content-Type: application/json
```

### Body (JSON)

```json
{
  "id": "1",
  "userID": "10",
  "userEmail": "user@email.com",
  "msgString": "Mensagem",
  "dateTime": "2026-06-16T10:00:00Z"
}
```

### Response

```json
{
  "success": true,
  "message": "Notification stored successfully"
}
```

---

## 3 - Consultar mensagem por ID

### Request

```http
GET /notification/{id}
```

### Response

```json
{
  "id": "1",
  "userID": "10",
  "userEmail": "user@email.com",
  "msgString": "Mensagem",
  "dateTime": "2026-06-16T10:00:00Z"
}
```



### Exemplo cURL

```bash
curl --request GET \
  --url http://localhost:8000/health-check
```

### Response

```json
{
  "success": "ok"
}
```

> O endpoint verifica se o serviço está ativo e pronto para receber requisições. Por enquanto, retorna um status simples de saúde. Validações mais detalhadas (conexão com broker, configurações) podem ser adicionadas posteriormente.

---

# 📊 Fluxo da Aplicação (futuro)

```text
Sistema Externo
   ↓
Envia mensagem via REST
   ↓
Serviço publica no Broker
   ↓
Consumidor processa
   ↓
Notificação é entregue ao destinatário
```

---

# 🔒 Segurança

* Sanitização de entrada
* Tratamento global de exceções
* Logs de auditoria
* Mascaramento de dados sensíveis (chaves de API, credenciais)

---

# 🧪 Testes

Executar testes automatizados:

```bash
mvn test
```

---

# 📦 Build para Produção

```bash
mvn clean package
```

Arquivo gerado:

```text
target/notification-service.jar
```

Executar:

```bash
java -jar target/notification-service.jar
```

---

# 📄 Licença

Projeto desenvolvido para fins educacionais e demonstração de arquitetura de microsserviços utilizando Java 21, Spring Boot e Maven.