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

1. Recebe a requisição do cliente
2. Consulta o Eureka para descobrir a instância do serviço de destino
3. Roteia a requisição com balanceamento de carga (`lb://`)
4. Retorna a resposta ao cliente

### Benefícios

✅ Ponto de entrada único para toda a plataforma

✅ Roteamento dinâmico via Eureka (sem IPs hardcoded)

✅ Balanceamento de carga automático

✅ Desacoplamento entre clientes e microsserviços

✅ Facilidade para adicionar cross-cutting concerns (autenticação, rate limiting, logging)

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
│       │       └── GatewayApplication.java
│       │
│       └── resources/
│           ├── application.yaml
│           └── logback-spring.xml
│
└── pom.xml
```

### Descrição dos Arquivos

| Arquivo                    | Função                                                      |
| -------------------------- | ----------------------------------------------------------- |
| `GatewayApplication.java`  | Classe principal com `@SpringBootApplication`               |
| `application.yaml`         | Rotas do Gateway, Eureka e configuração de observabilidade  |
| `logback-spring.xml`       | Configuração de logs com traceId/spanId e Logstash          |
| `pom.xml`                  | Dependências e gerenciamento do Maven                       |

---

# 🚀 Tecnologias Utilizadas

| Tecnologia                       | Versão   |
| -------------------------------- | -------- |
| Java                             | 17       |
| Spring Boot                      | 3.4.0    |
| Spring Cloud Gateway MVC         | 2024.0.0 |
| Spring Cloud Netflix Eureka Client | 2024.0.0 |
| Spring Boot Actuator             | 3.4.0    |
| Micrometer Tracing (OpenTelemetry) | Latest |
| Logstash Logback Encoder         | 8.0      |
| Maven                            | 3.8+     |

---

# 📋 Pré-requisitos

* JDK 17+
* Maven 3.8+
* Eureka Server rodando em `localhost:8761`

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

* Autenticação centralizada (JWT validation no Gateway)
* Rate Limiting por rota
* Circuit Breaker com Resilience4j
* CORS configurável
* Retry automático em falhas transientes
* Swagger/OpenAPI aggregation
* Filtros customizados de request/response

---

# 📄 Licença

Este projeto faz parte do ecossistema **CineLeo** e destina-se ao uso acadêmico e educacional.

---

## 👨‍💻 Desenvolvido para o Ecossistema CineLeo

API Gateway • Spring Cloud Gateway • Eureka Discovery • OpenTelemetry • Java 17 • Spring Boot 3
