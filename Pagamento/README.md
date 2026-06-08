# 💳 Payment API

API de pagamentos desenvolvida em **Java 21**, **Spring Boot 3** e **Maven**, responsável pelo gerenciamento de clientes, processamento de pagamentos via cartão de crédito/débito e consulta de status das transações.

## 🚀 Tecnologias

* Java 21
* Spring Boot 3
* Maven
* REST API
* JSON
* JUnit
* Docker (opcional)

---

# 📋 Funcionalidades

### Clientes

* Cadastro de clientes
* Validação de CPF
* Validação de e-mail
* Geração automática de identificador único

### Pagamentos

* Processamento de pagamentos via cartão de crédito
* Processamento de pagamentos via cartão de débito
* Geração de identificador único para transações
* Consulta de status de pagamento

### Monitoramento

* Consulta de pagamentos
* Rastreamento de transações
* Validação de operações

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
Database
```

### Controller

Responsável pelas rotas HTTP e comunicação com o cliente.

### DTO

Responsável pela transferência de dados entre camadas.

### Service

Responsável pelas regras de negócio.

### Repository

Responsável pelo acesso aos dados.

### Database

Persistência das informações.

---

# ⚙️ Executando o Projeto

## Clonar repositório

```bash
git clone https://github.com/seu-usuario/payment-api.git
```

```bash
cd payment-api
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
http://localhost:5000
```

---

# 📌 Endpoints

## 1. Criar Cliente

### Request

```http
POST /customers
```

### Body

```json
{
  "name": "João da Silva",
  "email": "joao@email.com",
  "cpf": "12345678909"
}
```

### Exemplo cURL

```bash
curl --request POST \
--url http://localhost:5000/customers \
--header 'Content-Type: application/json' \
--data '{
  "name": "João da Silva",
  "email": "joao@email.com",
  "cpf": "12345678909"
}'
```

### Response

```json
{
  "customerId": "cus_000008074616"
}
```

---

## 2. Processar Pagamento

### Request

```http
POST /payments/card
```

### Body

```json
{
  "customerId": "cus_000008074616",
  "billingType": "CREDIT_CARD",
  "value": 150.00,
  "description": "Compra teste",
  "card": {
    "number": "4111111111111111",
    "holderName": "João da Silva",
    "expiryMonth": "12",
    "expiryYear": "2027",
    "ccv": "123"
  }
}
```

### Exemplo cURL

```bash
curl --request POST \
--url http://localhost:5000/payments/card \
--header 'Content-Type: application/json' \
--data '{
  "customerId": "cus_000008074616",
  "billingType": "CREDIT_CARD",
  "value": 150.00,
  "description": "Compra teste",
  "card": {
    "number": "4111111111111111",
    "holderName": "João da Silva",
    "expiryMonth": "12",
    "expiryYear": "2027",
    "ccv": "123"
  }
}'
```

### Response

```json
{
  "status": "aprovado",
  "paymentId": "pay_98t6intmudl2dinc"
}
```

---

## 3. Consultar Status do Pagamento

### Request

```http
GET /payments/{paymentId}/status
```

### Exemplo cURL

```bash
curl --request GET \
--url http://localhost:5000/payments/pay_98t6intmudl2dinc/status
```

### Response

```json
{
  "approved": true
}
```

---

# 📊 Fluxo da Aplicação

```text
Cliente
   ↓
Criar Conta
   ↓
Receber customerId
   ↓
Criar Pagamento
   ↓
Receber paymentId
   ↓
Consultar Status
   ↓
Pagamento Aprovado
```

---

# 🔒 Segurança

* Validação de CPF
* Validação de e-mail
* Sanitização de entrada
* Tratamento global de exceções
* Logs de auditoria
* Mascaramento de dados sensíveis

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
target/payment-api.jar
```

Executar:

```bash
java -jar target/payment-api.jar
```

---

# 📄 Licença

Projeto desenvolvido para fins educacionais e demonstração de arquitetura de microsserviços utilizando Java 21, Spring Boot e Maven.
