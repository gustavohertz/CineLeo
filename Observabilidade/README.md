# 📊 Observabilidade Service - CineLeo

Microsserviço responsável pelo **monitoramento, observabilidade e centralização de logs** do ecossistema **CineLeo**.

Sua função é acompanhar continuamente a saúde dos demais microsserviços registrados no Eureka, disponibilizar métricas operacionais básicas e centralizar logs estruturados para facilitar análise, troubleshooting e acompanhamento do ambiente.

---

# 📋 Sumário

* [Visão Geral](#-visão-geral)
* [Arquitetura](#-arquitetura)
* [Estrutura do Projeto](#-estrutura-do-projeto)
* [Tecnologias Utilizadas](#-tecnologias-utilizadas)
* [Pré-requisitos](#-pré-requisitos)
* [Configuração](#-configuração)
* [Execução](#-execução)
* [Endpoints Disponíveis](#-endpoints-disponíveis)
* [Fluxo de Monitoramento](#-fluxo-de-monitoramento)
* [Serviços Monitorados](#-serviços-monitorados)
* [Build e Containerização](#-build-e-containerização)
* [Testes](#-testes)
* [Melhorias Futuras](#-melhorias-futuras)
* [Licença](#-licença)

---

# 🔍 Visão Geral

O **Observabilidade Service** não participa diretamente das regras de negócio da plataforma, mas fornece uma visão centralizada sobre o estado operacional do ambiente.

### Principais funcionalidades

✅ Monitoramento periódico dos microsserviços

✅ Integração com Eureka Discovery Server

✅ Dashboard consolidado de disponibilidade

✅ Coleta de logs estruturados

✅ Filtro de logs por serviço

✅ Filtro de logs por nível

✅ Cache em memória dos eventos recentes

✅ Endpoints de monitoramento e diagnóstico

---

# 🏗 Arquitetura

```text
                           ┌─────────────────────┐
                           │ Observabilidade API │
                           │      :8090          │
                           └──────────┬──────────┘
                                      │
                                      ▼

                           ┌─────────────────────┐
                           │    Eureka Server    │
                           │       :8761         │
                           └──────────┬──────────┘
                                      │

        ┌─────────────┬───────────────┼───────────────┬─────────────┐
        ▼             ▼               ▼               ▼             ▼

 usuarios-service eventos-service payment-service notification-service Kafka

```

### Fluxo Operacional

1. O scheduler executa verificações periódicas.
2. O Eureka fornece as instâncias registradas.
3. O serviço consulta endpoints de saúde.
4. Os resultados são armazenados em memória.
5. O dashboard consolida os dados.
6. Logs enviados pelos serviços são centralizados para consulta.

---

# 📁 Estrutura do Projeto

```text
src/
└── main/
    ├── java/
    │   └── com/cineleo/observabilidade/
    │
    ├── controller/
    │   ├── DashboardController.java
    │   ├── HealthCheckController.java
    │   └── LogController.java
    │
    ├── service/
    │   ├── HealthCheckService.java
    │   └── LogService.java
    │
    ├── dto/
    │   ├── DashboardDTO.java
    │   ├── ServicoStatusDTO.java
    │   └── LogEventDTO.java
    │
    └── scheduler/
        └── HealthCheckScheduler.java

src/main/resources/
└── application.properties

pom.xml
Dockerfile
```

---

# 🚀 Tecnologias Utilizadas

| Tecnologia                  | Versão   |
| --------------------------- | -------- |
| Java                        | 21       |
| Spring Boot                 | 3.5.3    |
| Spring Cloud Netflix Eureka | 2025.0.0 |
| Spring Boot Actuator        | Latest   |
| Lombok                      | Latest   |
| Maven                       | 3.8+     |
| Docker                      | Opcional |

---

# 📋 Pré-requisitos

Antes de iniciar o projeto, certifique-se de possuir:

* JDK 21
* Maven 3.8+
* Eureka Server em execução
* Docker (opcional)

Verificação das versões:

```bash
java -version
mvn -version
docker --version
```

---

# ⚙️ Configuração

## application.properties

```properties
spring.application.name=observabilidade-service
server.port=8090

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.restclient.enabled=true

management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always

observabilidade.check-interval=30000
observabilidade.servicos=usuarios-service,eventos-service,paymentService,notification-service,microservicesKafka
```

### Configurações Personalizadas

| Propriedade                                 | Descrição                         |
| ------------------------------------------- | --------------------------------- |
| `observabilidade.check-interval`            | Intervalo entre verificações (ms) |
| `observabilidade.servicos`                  | Lista de serviços monitorados     |
| `management.endpoints.web.exposure.include` | Endpoints Actuator expostos       |

---

# ▶️ Execução

## Iniciar Infraestrutura

Certifique-se de que o Eureka Server esteja disponível:

```bash
docker compose up -d eureka-server
```

## Executar Aplicação

```bash
mvn spring-boot:run
```

A aplicação ficará disponível em:

```text
http://localhost:8090
```

---

# 🔌 Endpoints Disponíveis

## Health Check

### Request

```http
GET /health-check
```

### Response

```json
{
  "success": "ok"
}
```

---

## Dashboard Geral

### Request

```http
GET /observabilidade/dashboard
```

### Exemplo de Resposta

```json
{
  "totalServicos": 5,
  "servicosOnline": 4,
  "servicosOffline": 1,
  "ultimaVerificacao": "2026-06-22T12:00:00"
}
```

---

## Status dos Serviços

### Request

```http
GET /observabilidade/status
```

Retorna uma coleção de objetos `ServicoStatusDTO`.

---

## Registrar Log

### Request

```http
POST /observabilidade/logs
Content-Type: application/json
```

### Body

```json
{
  "servico": "eventos-service",
  "nivel": "INFO",
  "mensagem": "Reserva criada com sucesso"
}
```

### Response

```http
200 OK
```

---

## Consultar Logs

### Todos os Logs

```http
GET /observabilidade/logs
```

### Logs por Serviço

```http
GET /observabilidade/logs/servico/eventos-service
```

### Logs por Nível

```http
GET /observabilidade/logs/nivel/ERROR
```

---

# 🧪 Exemplos cURL

## Dashboard

```bash
curl -X GET http://localhost:8090/observabilidade/dashboard
```

## Status dos Serviços

```bash
curl -X GET http://localhost:8090/observabilidade/status
```

## Registrar Log

```bash
curl -X POST http://localhost:8090/observabilidade/logs \
-H "Content-Type: application/json" \
-d '{
  "servico":"eventos-service",
  "nivel":"INFO",
  "mensagem":"Teste de log"
}'
```

## Consultar Logs

```bash
curl -X GET http://localhost:8090/observabilidade/logs
```

---

# 🔄 Fluxo de Monitoramento

```text
Scheduler
    │
    ▼

Obtém lista de serviços
    │
    ▼

Consulta Eureka
    │
    ▼

Descobre instâncias
    │
    ▼

GET /actuator/health
    │
    ├── Sucesso → Status UP
    │
    └── Falha
            │
            ▼

    GET /health-check
            │
            ├── Sucesso → Status UP
            │
            └── Falha → Status DOWN
```

### Requisitos para Monitoramento

Cada microsserviço deve disponibilizar ao menos um dos endpoints:

```text
/actuator/health
```

ou

```text
/health-check
```

Caso ambos falhem, o serviço será considerado indisponível.

---

# 📡 Serviços Monitorados

Por padrão:

* usuarios-service
* eventos-service
* paymentService
* notification-service
* microservicesKafka

Configurados através da propriedade:

```properties
observabilidade.servicos
```

---

# 🐳 Build e Containerização

## Gerar JAR

```bash
mvn clean package
```

## Executar JAR

```bash
java -jar target/observabilidade-service.jar
```

## Criar Imagem Docker

```bash
docker build -t observabilidade-service .
```

## Executar Container

```bash
docker run -p 8090:8090 observabilidade-service
```

---

# ✅ Testes

Executar todos os testes automatizados:

```bash
mvn test
```

Cobertura principal:

* Registro de logs
* Consulta de logs
* Filtros por serviço
* Filtros por nível
* Controle de limite de memória (500 registros)

---

# 🔮 Melhorias Futuras

* Persistência dos logs em PostgreSQL
* Integração com Prometheus
* Dashboards Grafana
* Alertas por e-mail
* Alertas por Slack
* Exportação de métricas
* Interface web dedicada
* Autenticação e autorização
* Histórico de disponibilidade dos serviços
* Relatórios operacionais

---

# 📄 Licença

Projeto desenvolvido para fins acadêmicos e educacionais como parte do ecossistema **CineLeo**.

---

## 👨‍💻 Desenvolvido para o Ecossistema CineLeo

Observabilidade • Monitoramento • Logging • Spring Boot • Eureka Discovery • Java 21
