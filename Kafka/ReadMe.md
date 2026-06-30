# 🔁 Microservices Kafka - CineLeo

Microsserviço responsável pela **mensageria, processamento de eventos e envio real de e-mails** do ecossistema **CineLeo**.

Atua como consumidor de eventos Kafka relacionados a pagamentos e notificações, além de ser responsável pelo envio assíncrono de e-mails utilizando SMTP através do Spring Mail.

---

# 📋 Sumário

* [Visão Geral](#-visão-geral)
* [Arquitetura](#-arquitetura)
* [Estrutura do Projeto](#-estrutura-do-projeto)
* [Tecnologias Utilizadas](#-tecnologias-utilizadas)
* [Pré-requisitos](#-pré-requisitos)
* [Configuração](#-configuração)
* [Execução](#-execução)
* [Tópicos Kafka](#-tópicos-kafka)
* [Integrações](#-integrações)
* [Fluxo de Processamento](#-fluxo-de-processamento)
* [Endpoints Disponíveis](#-endpoints-disponíveis)
* [Build e Containerização](#-build-e-containerização)
* [Testes](#-testes)
* [Melhorias Futuras](#-melhorias-futuras)
* [Licença](#-licença)

---

# 🔍 Visão Geral

O **Microservices Kafka** é responsável por processar eventos assíncronos dentro da arquitetura de microsserviços do CineLeo.

### Principais responsabilidades

✅ Consumir eventos de pagamento

✅ Criar notificações automaticamente

✅ Processar solicitações de envio de e-mails

✅ Realizar envio SMTP real

✅ Confirmar entregas via Kafka

✅ Registrar falhas de envio

✅ **Retry Automático com Exponential Backoff**

✅ **Tratamento via Dead Letter Queue (DLQ)**

✅ Integrar Notification Service ao Kafka

✅ Garantir desacoplamento entre serviços

---

# 🏗 Arquitetura

```text
                      Eventos Service
                             │
                             │
         ┌───────────────────┴───────────────────┐
         │                                       │

 cinema.pagamento.aprovado        cinema.pagamento.recusado
         │                                       │
         └───────────────────┬───────────────────┘
                             ▼

                 ┌──────────────────────┐
                 │  Microservices Kafka │
                 │        :8081         │
                 └──────────┬───────────┘
                            │

         ┌──────────────────┼──────────────────┐
         │                  │                  │
         ▼                  ▼                  ▼

 Notification       notification.email.send   SMTP
    Service                 Topic          JavaMailSender
       │                                      │
       └──────────────► notification.email.sent
```

---

# 📁 Estrutura do Projeto

```text
src/
└── main/
    ├── java/
    │   └── com/cineleo/kafka/
    │
    ├── consumer/
    │   ├── PaymentApprovedConsumer.java
    │   ├── PaymentDeniedConsumer.java
    │   └── EmailConsumer.java
    │
    ├── producer/
    │   └── EmailStatusProducer.java
    │
    ├── service/
    │   ├── NotificationService.java
    │   └── EmailSenderService.java
    │
    ├── client/
    │   └── NotificationClient.java
    │
    └── dto/
        ├── PaymentEventDTO.java
        ├── EmailRequestDTO.java
        └── EmailStatusDTO.java

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
| Spring Boot                 | 3.4.0    |
| Spring Kafka                | Latest   |
| Spring Mail                 | Latest   |
| Spring Cloud Netflix Eureka | 2024.0.0 |
| Spring Boot Actuator        | Latest   |
| Lombok                      | Latest   |
| Maven                       | 3.8+     |
| Docker                      | Opcional |

---

# 📋 Pré-requisitos

Antes de executar o projeto:

* JDK 21
* Maven 3.8+
* Apache Kafka
* Eureka Server
* Notification Service
* Conta SMTP válida
* Docker (opcional)

Infraestrutura padrão:

| Serviço              | Porta |
| -------------------- | ----- |
| Eureka Server        | 8761  |
| Kafka                | 9092  |
| Notification Service | 8000  |
| Microservices Kafka  | 8081  |

---

# ⚙️ Configuração

## application.properties

```properties
spring.application.name=microservicesKafka
server.port=8081

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=cineleo-notificacoes
spring.kafka.consumer.auto-offset-reset=earliest

spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer

spring.kafka.consumer.properties.spring.json.trusted.packages=com.infnet.microservicesKafka.dto,com.leocine.entity

spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer

services.notification.url=http://localhost:8000

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seuemail@gmail.com
spring.mail.password=sua_senha_app

spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

---

## Configuração SMTP

### Gmail

Utilize uma senha de aplicativo:

```text
Google Account
 └── Segurança
      └── Senhas de App
```

⚠️ Não utilize a senha principal da conta Google.

---

# ▶️ Execução

## Subir Infraestrutura

```bash
docker compose up -d kafka eureka-server
```

---

## Executar Aplicação

```bash
mvn spring-boot:run
```

ou execute a classe:

```text
MicroservicesKafkaApplication
```

Após iniciar, o serviço será registrado automaticamente no Eureka como:

```text
MICROSERVICESKAFKA
```

---

# 📨 Tópicos Kafka

| Tópico                      | Tipo     | Descrição                      |
| --------------------------- | -------- | ------------------------------ |
| `cinema.pagamento.aprovado` | Consumer | Pagamentos aprovados           |
| `cinema.pagamento.recusado` | Consumer | Pagamentos recusados           |
| `notification.email.send`   | Consumer | Solicitação de envio de e-mail |
| `notification.email.sent`   | Producer | Resultado do envio             |

---

# 🔗 Integrações

## Notification Service

### Criar Notificação

```http
POST /notification/consume
```

Utilizado para registrar eventos de pagamento.

---

### Solicitar Envio de E-mail

```http
POST /notification/send-email/{id}
```

Mantido por compatibilidade com o fluxo atual.

---

## Servidor SMTP

Responsável pelo envio efetivo das mensagens.

Fluxo:

```text
Kafka Topic
      │
      ▼

EmailConsumer
      │
      ▼

EmailSenderService
      │
      ▼

JavaMailSender
      │
      ▼

Servidor SMTP
      │
      ▼

Destinatário
```

---

# 🔄 Fluxo de Processamento

## Pagamento Aprovado

```text
Eventos Service
      │
      ▼

cinema.pagamento.aprovado
      │
      ▼

Microservices Kafka
      │
      ▼

Notification Service
      │
      ▼

notification.email.send
      │
      ▼

SMTP
      │
      ▼

notification.email.sent
```

---

## 🔁 Tolerância a Falhas (Retry e DLQ)

O serviço implementa tolerância a falhas utilizando as anotações nativas do Spring Kafka (`@RetryableTopic`).

* **Exponential Backoff:** Em caso de falha de processamento ou de rede (ex: SMTP indisponível), o consumo do evento não é perdido. O sistema tenta processar a mensagem múltiplas vezes, aumentando o tempo de espera a cada tentativa.
    * *Exemplo (Pagamentos):* 5 tentativas, com tempo de espera crescendo exponencialmente (2s, 3s, 4.5s...).
* **Dead Letter Queue (DLQ):** Se a mensagem exceder o limite máximo de tentativas de *Retry*, ela é redirecionada automaticamente para um tópico de mensagens mortas (`.DLT`), evitando que a fila principal trave e permitindo análise manual posterior.

---

## Pagamento Recusado

```text
Eventos Service
      │
      ▼

cinema.pagamento.recusado
      │
      ▼

Notification Service
      │
      ▼

Envio de E-mail
```

---

# 🌐 Endpoints Disponíveis

| Método | Endpoint           | Descrição                |
| ------ | ------------------ | ------------------------ |
| GET    | `/actuator/health` | Status da aplicação      |
| GET    | `/actuator/info`   | Informações da aplicação |

---

## Health Check

### Request

```http
GET /actuator/health
```

### Response

```json
{
  "status": "UP"
}
```

---

# 🧪 Exemplos cURL

## Health Check

```bash
curl -X GET http://localhost:8081/actuator/health
```

---

## Informações da Aplicação

```bash
curl -X GET http://localhost:8081/actuator/info
```

---

# 🐳 Build e Containerização

## Gerar JAR

```bash
mvn clean package
```

---

## Executar JAR

```bash
java -jar target/microservices-kafka.jar
```

---

## Construir Imagem Docker

```bash
docker build -t microservices-kafka .
```

---

## Executar Container

```bash
docker run -p 8081:8081 microservices-kafka
```

---

# ✅ Testes

Executar todos os testes automatizados:

```bash
mvn test
```

Principais cenários:

* Consumo de mensagens Kafka
* Processamento de eventos
* Integração Notification Service
* Envio SMTP
* Tratamento de erros
* Publicação de status de entrega

---

# 🔮 Melhorias Futuras

* Integração Prometheus + Grafana
* Templates HTML para e-mail
* Rastreamento de entregas
* Métricas detalhadas de consumo Kafka
* Suporte a múltiplos provedores SMTP
* Circuit Breaker com Resilience4j

---

# 📄 Licença

Projeto desenvolvido para fins acadêmicos e educacionais como parte do ecossistema **CineLeo**.

---

## 👨‍💻 Desenvolvido para o Ecossistema CineLeo

Kafka • Event Driven Architecture • SMTP • Spring Boot • Spring Kafka • Java 21
