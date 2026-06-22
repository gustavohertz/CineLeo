# 🎬 CineLeo Eureka Server

Serviço de descoberta (**Service Registry**) do ecossistema **CineLeo**, baseado no **Netflix Eureka**. É responsável por manter um catálogo atualizado de todos os microsserviços ativos, permitindo comunicação dinâmica e desacoplada entre eles.

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
* [Registro de Microsserviços](#-registro-de-microsserviços)
* [Monitoramento](#-monitoramento)
* [Melhorias Futuras](#-melhorias-futuras)
* [Licença](#-licença)

---

# 🔍 Visão Geral

O **Eureka Server** funciona como um diretório central para os microsserviços do ecossistema CineLeo.

Quando um serviço é iniciado, ele se registra automaticamente no Eureka. Outros serviços podem então localizar e consumir suas APIs sem precisar conhecer endereços IP ou portas específicas.

### Benefícios

✅ Descoberta automática de serviços
✅ Redução de configurações manuais
✅ Balanceamento de carga integrado com Spring Cloud
✅ Maior escalabilidade da arquitetura
✅ Comunicação desacoplada entre microsserviços

---

# 🏗 Arquitetura

```text
                    ┌─────────────────┐
                    │  Eureka Server  │
                    │     :8761       │
                    └────────┬────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
         ▼                   ▼                   ▼

 ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
 │ User Service │   │ Movie Service│   │ Payment API  │
 └──────────────┘   └──────────────┘   └──────────────┘

         Todos os serviços registram-se no Eureka
```

### Componente Principal

| Componente    | Responsabilidade                                                                  |
| ------------- | --------------------------------------------------------------------------------- |
| Eureka Server | Registrar e disponibilizar informações sobre instâncias ativas dos microsserviços |

---

# 📁 Estrutura do Projeto

```text
src/
└── main/
    ├── java/
    │   └── com/eureka/
    │       └── EurekaApplication.java
    │
    └── resources/
        └── application.yaml

pom.xml
```

### Descrição dos Arquivos

| Arquivo                  | Função                                             |
| ------------------------ | -------------------------------------------------- |
| `EurekaApplication.java` | Classe principal anotada com `@EnableEurekaServer` |
| `application.yaml`       | Configurações da aplicação                         |
| `pom.xml`                | Dependências e gerenciamento do Maven              |

---

# 🚀 Tecnologias Utilizadas

| Tecnologia                  | Versão   |
| --------------------------- | -------- |
| Java                        | 21       |
| Spring Boot                 | 3.4.0    |
| Spring Cloud Netflix Eureka | 2024.0.0 |
| Maven                       | 3.8+     |

---

# 📋 Pré-requisitos

Antes de executar o projeto, certifique-se de possuir:

* JDK 21
* Maven 3.8 ou superior
* Git (opcional)

Verifique as versões instaladas:

```bash
java -version
mvn -version
```

---

# ⚙️ Configuração

## application.yaml

```yaml
spring:
  application:
    name: eureka-server

server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

### Configurações importantes

| Configuração                  | Descrição                               |
| ----------------------------- | --------------------------------------- |
| `register-with-eureka: false` | Impede que o próprio Eureka se registre |
| `fetch-registry: false`       | Impede consulta ao próprio catálogo     |
| `port: 8761`                  | Porta padrão do Eureka Server           |

---

# ▶️ Execução

## Executar via Maven

```bash
mvn spring-boot:run
```

## Gerar JAR

```bash
mvn clean package
```

## Executar JAR

```bash
java -jar target/eureka-server.jar
```

---

# 🌐 Painel Administrativo

Após iniciar a aplicação:

```text
http://localhost:8761
```

A interface permite visualizar:

* Serviços registrados
* Instâncias disponíveis
* Status de saúde dos microsserviços
* Informações de replicação

---

# 🔌 Endpoints Disponíveis

| Método | Endpoint           | Descrição                       |
| ------ | ------------------ | ------------------------------- |
| GET    | `/`                | Dashboard do Eureka             |
| GET    | `/eureka/apps`     | Lista de aplicações registradas |
| GET    | `/actuator/health` | Verificação de saúde            |
| GET    | `/actuator/info`   | Informações da aplicação        |

---

# 🔗 Registro de Microsserviços

Todo microsserviço que desejar participar da arquitetura deve incluir o cliente Eureka.

## Dependência Maven

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

## application.properties

```properties
spring.application.name=user-service

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.restclient.enabled=true
```

## Fluxo de Registro

```text
Microsserviço inicia
          │
          ▼
Conecta ao Eureka
          │
          ▼
Realiza registro
          │
          ▼
Disponível para descoberta
          │
          ▼
Consumido por outros serviços
```

---

# 📈 Monitoramento

Endpoints expostos:

```text
/actuator/health
/actuator/info
```

Exemplo:

```bash
curl http://localhost:8761/actuator/health
```

Resposta:

```json
{
  "status": "UP"
}
```

---

# 🔮 Melhorias Futuras

* Implementação de autenticação no painel Eureka
* Cluster com múltiplas instâncias para alta disponibilidade
* Integração com Spring Boot Admin
* Observabilidade com Micrometer e Prometheus
* Dashboards utilizando Grafana
* Deploy automatizado via CI/CD
* Configuração centralizada com Spring Cloud Config

---

# 📄 Licença

Este projeto faz parte do ecossistema **CineLeo** e destina-se ao uso interno da plataforma.

---

## 👨‍💻 Desenvolvido para o Ecossistema CineLeo

Service Discovery • Spring Cloud Netflix Eureka • Java 21 • Spring Boot 3
