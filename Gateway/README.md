# 🎬 CineLeo API Gateway

Serviço de **API Gateway** do ecossistema **CineLeo**, baseado no **Spring Cloud Gateway MVC**. É o ponto de entrada único para todas as requisições externas, roteando-as para os microsserviços internos através do **Eureka Service Discovery** com balanceamento de carga.

---

# 📋 Sumário

* [Visão Geral](#-visão-geral)
* [Arquitetura](#-arquitetura)
* [Estrutura do Projeto](#-estrutura-do-projeto)
* [Tecnologias Utilizadas](#-tecnologias-utilizadas)
* [Pré-requisitos](#-pré-requisitos)
* [Configuração](#-configuração)
* [Execução](#-execução)
* [Rotas Configuradas](#-rotas-configuradas)
* [Endpoints Disponíveis](#-endpoints-disponíveis)
* [Observabilidade](#-observabilidade)
* [Melhorias Futuras](#-melhorias-futuras)
* [Licença](#-licença)

---

# 🔍 Visão Geral

O **API Gateway** centraliza o acesso aos microsserviços do CineLeo em um único endpoint (`localhost:9999`). Em vez de cada cliente conhecer a porta e endereço de cada serviço, todas as requisições passam pelo Gateway, que:

1. Recebe a requisição do cliente.
2. Verifica se a rota é pública ou protegida.
3. Valida o token JWT HS256 nas rotas protegidas.
4. Consulta o Eureka para descobrir a instância do serviço de destino.
5. Roteia a requisição com balanceamento de carga (lb://).
6. Retorna a resposta ao cliente.

### Benefícios

✅ Ponto de entrada único para toda a plataforma

✅ Roteamento dinâmico via Eureka (sem IPs hardcoded)

✅ Balanceamento de carga automático

✅ Desacoplamento entre clientes e microsserviços

✅ Autenticação centralizada e validação de tokens JWT

✅ Facilidade para adicionar rate limiting, logging e outros recursos compartilhados

✅ Tracing distribuído com OpenTelemetry

---

# 🏗 Arquitetura

```text
                          Cliente (CLI / Browser / Postman)
                                       │
                                       ▼
                              ┌─────────────────┐
                              │  API Gateway    │
                              │     :9999       │
                              └────────┬────────┘
                                       │
                              ┌────────┴────────┐
                              │  Eureka Server  │
                              │     :8761       │
                              └────────┬────────┘
                                       │
        ┌──────────┬──────────┬────────┼────────┬──────────┬──────────┐
        │          │          │        │        │          │          │
        ▼          ▼          ▼        ▼        ▼          ▼          ▼
   ┌─────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌──────────┐ ┌──────────┐
   │Eventos  │ │Usuarios│ │Pagamento│ │Notific.│ │  Kafka   │ │Observab. │
   │ :8082   │ │ :8083  │ │ :5000  │ │ :8000  │ │  :8081   │ │  :8090   │
   └─────────┘ └────────┘ └────────┘ └────────┘ └──────────┘ └──────────┘
```

### Fluxo de Roteamento

```text
Cliente envia GET /api/eventos/filmes
          │
          ▼
Gateway recebe em :9999
          │
          ▼
Predicate: Path=/api/eventos/**
          │
          ▼
Filter: StripPrefix=2 (remove /api/eventos)
          │
          ▼
URI: lb://EVENTOS-SERVICE (Eureka resolve para :8082)
          │
          ▼
Eventos Service recebe GET /filmes
          │
          ▼
Resposta retorna ao cliente via Gateway
```

---

# 📁 Estrutura do Projeto

```text
Gateway/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/cineleo/gateway/
│       │       ├── FallbackController.java
│       │       ├── GatewayApplication.java
│       │       └── SecurityConfig.java
│       │
│       └── resources/
│           ├── application.yaml
│           └── logback-spring.xml
│
└── pom.xml
```

### Descrição dos Arquivos

| Arquivo                    | Função                                                            |
| -------------------------- | ----------------------------------------------------------------- |
| `FallbackController.java`  | Retorna respostas de fallback quando um serviço está indisponível |
| `GatewayApplication.java`  | Classe principal com `@SpringBootApplication`                     |
| `SecurityConfig.java`      | Define as rotas públicas e protegidas e valida tokens JWT HS256   |
| `application.yaml`         | Rotas do Gateway, Eureka e configuração de observabilidade        |
| `logback-spring.xml`       | Configuração de logs com traceId/spanId e Logstash                |
| `pom.xml`                  | Dependências e gerenciamento do Maven                             |

---

# 🚀 Tecnologias Utilizadas

| Tecnologia                         | Versão   |
| ---------------------------------- | -------- |
| Java                               | 17       |
| Spring Boot                        | 3.4.0    |
| Spring Cloud Gateway MVC           | 2024.0.0 |
| Spring Cloud Netflix Eureka Client | 2024.0.0 |
| Spring Boot Actuator               | 3.4.0    |
| Micrometer Tracing (OpenTelemetry) | Latest   |
| Logstash Logback Encoder           | 8.0      |
| Spring Security                    | 6.x      |
| Spring OAuth2 Resource Server      | 6.x      |
| JWT HS256                          | -        |
| Resilience4j                       | Latest   |
| Maven                              | 3.8+     |

---

# 📋 Pré-requisitos

* JDK 17+
* Maven 3.8+
* Eureka Server rodando em `localhost:8761`
* Mesma variável `JWT_SECRET` configurada no Gateway e no Usuarios Service

Verifique as versões instaladas:

```bash
java -version
mvn -version
```

---

# ⚙️ Configuração

## application.yaml

### Rotas

Cada rota segue o padrão:

```yaml
- id: nome-do-servico
  uri: lb://NOME-NO-EUREKA
  predicates:
    - Path=/api/prefixo/**
  filters:
    - StripPrefix=2
```

* **`uri: lb://`** — usa o balanceador de carga do Eureka para resolver o endereço
* **`StripPrefix=2`** — remove os dois primeiros segmentos do path (`/api/prefixo`) antes de encaminhar

### Eureka

```yaml
spring:
  eureka:
    client:
      service-url:
        defaultZone: http://localhost:8761/eureka/
```

### Observabilidade

O Gateway inclui tracing distribuído via OpenTelemetry com exportação OTLP, e logs estruturados com traceId/spanId para correlação entre serviços.

### Autenticação JWT

O Gateway atua como um Resource Server e valida os tokens JWT enviados nas requisições protegidas.

Os tokens são:

* Gerados pelo `Usuarios Service`.
* Assinados com o algoritmo HS256.
* Validados pelo Gateway usando a mesma chave secreta.
* Enviados pelo cliente no cabeçalho Authorization.

Exemplo:

Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

A chave é configurada no `application.yaml`:

```yaml
jwt:
  secret: "${JWT_SECRET:cineleo-chave-secreta-local-123456789012345}"
```

O `Usuarios Service` e o Gateway devem utilizar exatamente o mesmo valor para `JWT_SECRET`.

Em ambiente local, o valor após os dois-pontos funciona como chave padrão. Em um ambiente real, a chave não deve ser salva diretamente no repositório; ela deve ser fornecida por variável de ambiente ou por um gerenciador de segredos.

Exemplo de variável de ambiente no PowerShell:
```bash
$env:JWT_SECRET="uma-chave-secreta-com-pelo-menos-32-caracteres"
mvn spring-boot:run
```

Exemplo no CMD:
```bash
set JWT_SECRET=uma-chave-secreta-com-pelo-menos-32-caracteres
mvn spring-boot:run
```

---

# Rotas Públicas e Protegidas

## Rotas públicas

As seguintes rotas podem ser acessadas sem token:

* POST /api/usuarios/login
* POST /api/usuarios/create

* GET /api/eventos/filmes/**  
* GET /api/eventos/salas/**  
* GET /api/eventos/sessoes/**

* GET /actuator/**
* GET /fallback/**

O login e o cadastro são públicos porque o usuário ainda não possui um token nesse momento. As consultas ao catálogo também foram definidas como públicas.

## Rotas protegidas

Todas as demais rotas exigem um token JWT válido:

```
Authorization: Bearer <token>
```

Quando o token não é enviado, está expirado, possui uma assinatura inválida ou apresenta um emissor diferente do esperado, o Gateway bloqueia a requisição antes de encaminhá-la ao microsserviço de destino.

---

# ▶️ Execução

## Executar via Maven

```bash
cd Gateway
mvn spring-boot:run
```

## Gerar JAR

```bash
mvn clean package
```

## Executar JAR

```bash
java -jar target/Gateway-1.0-SNAPSHOT.jar
```

O Gateway estará disponível em:

```text
http://localhost:9999
```

---

# 🔀 Rotas Configuradas

| Rota                        | Serviço de Destino     | Nome no Eureka           | Porta |
| --------------------------- | ---------------------- | ------------------------ | ----- |
| `/api/eventos/**`           | Eventos Service        | `EVENTOS-SERVICE`        | 8082  |
| `/api/usuarios/**`          | Usuarios Service       | `USUARIOS-SERVICE`       | 8083  |
| `/api/pagamentos/**`        | Pagamento Service      | `PAYMENTSERVICE`         | 5000  |
| `/api/notificacoes/**`      | Notification Service   | `NOTIFICATION-SERVICE`   | 8000  |
| `/api/kafka/**`             | Microservices Kafka    | `MICROSERVICESKAFKA`     | 8081  |
| `/api/observabilidade/**`   | Observabilidade Service| `OBSERVABILIDADE-SERVICE`| 8090  |

### Exemplos de Mapeamento

| Requisição via Gateway                          | Roteado para                                |
| ----------------------------------------------- | ------------------------------------------- |
| `GET localhost:9999/api/eventos/filmes`          | `GET EVENTOS-SERVICE/filmes`                |
| `POST localhost:9999/api/usuarios/login`         | `POST USUARIOS-SERVICE/login`               |
| `POST localhost:9999/api/pagamentos/payments/card`| `POST PAYMENTSERVICE/payments/card`         |
| `GET localhost:9999/api/notificacoes/notification/1` | `GET NOTIFICATION-SERVICE/notification/1` |
| `GET localhost:9999/api/observabilidade/dashboard`| `GET OBSERVABILIDADE-SERVICE/dashboard`     |

---

# 🌐 Endpoints Disponíveis

## Actuator

| Método | Endpoint              | Descrição                          |
| ------ | --------------------- | ---------------------------------- |
| GET    | `/actuator/health`    | Verificação de saúde do Gateway    |
| GET    | `/actuator/info`      | Informações da aplicação           |
| GET    | `/actuator/gateway`   | Rotas configuradas no Gateway      |

---

# 🔐 Fluxo de Autenticação

## Login

Cliente  
   │  
   │ POST /api/usuarios/login  
   ▼  
API Gateway  
   │  
   │ rota pública  
   ▼  
Usuarios Service  
   │  
   ├── busca usuário pelo e-mail  
   ├── verifica se o usuário está ativo  
   ├── compara a senha com o hash BCrypt  
   └── gera um token JWT HS256  
            │  
            ▼  
        Token JWT  

## Acesso a uma rota protegida  
Cliente  
   │  
   │ Authorization: Bearer <token>  
   ▼  
API Gateway  
   │  
   ├── valida a assinatura HS256  
   ├── valida a expiração  
   ├── valida o issuer "auth-service"  
   └── verifica se a rota exige autenticação  
            │  
            ▼  
      Microsserviço de destino  

O Gateway não precisa consultar o Usuarios Service a cada requisição. A validação é feita localmente usando a mesma chave secreta utilizada para assinar o token.

---

# 📊 Observabilidade

O Gateway está configurado com:

* **Tracing distribuído** — OpenTelemetry com propagação W3C
* **Exportação OTLP** — envio de traces para `localhost:4318`
* **Logs estruturados** — padrão com `traceId` e `spanId` para correlação
* **Logstash** — envio de logs para ELK Stack via `localhost:5001`

### Logging

```text
2026-06-22 10:30:00.000  INFO [api-gateway,traceId123,spanId456] [http-nio-9999-exec-1] c.c.g.GatewayApplication - ...
```

---

# 🔮 Melhorias Futuras

* Rate Limiting por rota
* CORS configurável
* Retry automático em falhas transientes
* Swagger/OpenAPI aggregation
* Filtros customizados de request/response

---

# 📄 Licença

Este projeto faz parte do ecossistema **CineLeo** e destina-se ao uso acadêmico e educacional.

---

## 👨‍💻 Desenvolvido para o Ecossistema CineLeo

API Gateway • Spring Cloud Gateway • Spring Security • JWT HS256 • Resilience4j • Eureka Discovery • OpenTelemetry • Java 17 • Spring Boot 3
