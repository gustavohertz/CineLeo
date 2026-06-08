# 🚀 Estudo de Microsserviços: Spring Boot, Eureka & Apache Kafka

Este repositório documenta um projeto de estudo prático focado na construção de uma arquitetura de microsserviços moderna utilizando **Java 21**, **Spring Boot 3.4.0** e **Spring Cloud 2024.0.0**. O objetivo principal é demonstrar a comunicação assíncrona orientada a eventos com **Apache Kafka** e o registro dinâmico de serviços com o **Netflix Eureka**.

## 🛠️ Tecnologias Utilizadas

*   **Java 21**
*   **Spring Boot 3.4.0**
*   **Spring Cloud 2024.0.0** (Netflix Eureka Server & Client)
*   **Spring for Apache Kafka**
*   **Apache Kafka (KRaft Mode)**
*   **Docker & Docker Compose**
*   **Maven**

## 🏗️ Arquitetura do Projeto

O ecossistema é composto por três blocos principais:

1.  **Eureka Server (`/microservices`):** Atua como o *Service Registry* da arquitetura. Ele roda na porta `8761` e mantém um catálogo de todos os microsserviços ativos na rede.
2.  **Microsserviço Kafka (`/microservicesKafka`):** Atua como um *Eureka Client* (rodando na porta `8081`). Ele se registra automaticamente no Eureka e contém serviços configurados para atuar como Produtor (`KafkaProducerService`) e Consumidor (`kafkaConsumerService`) de mensagens.
3.  **Infraestrutura Kafka (`docker-compose.yml`):** Um broker do Apache Kafka rodando via Docker utilizando o modo moderno **KRaft** (eliminando a necessidade do Zookeeper), expondo a porta `9092`.

---

## 💡 Principais Aprendizados e Soluções de Problemas

Durante o desenvolvimento deste estudo, lidamos com transições importantes do ecossistema Spring:

*   **Conflito de Cliente Eureka (Jersey vs RestClient):** Nas versões mais recentes do Spring Cloud (2024.x) integradas ao Spring Boot 3.4.x, o cliente Eureka tenta buscar bibliotecas antigas do Jersey (`TransportClientFactories`), gerando o erro `java.lang.NoClassDefFoundError`.
    *   *Solução:* Excluímos a dependência `eureka-client-jersey3` do `pom.xml` e forçamos o uso do cliente HTTP moderno do Spring através da propriedade `eureka.client.restclient.enabled=true`.
*   **Gerenciamento de Versões (Kafka):** Forçar versões Release Candidate (RC) no `spring-kafka` causa conflitos com o motor interno do Kafka (`kafka-clients`) providenciado pelo Spring Boot.
    *   *Solução:* Delegar o controle absoluto de versões para a tag `<parent>` do Spring Boot no `pom.xml`.
*   **Testes vs Service Registry:** Executar o build do Maven (`mvn install`) aciona os testes de contexto. Se o Eureka estiver habilitado durante o teste, ele tenta conectar na rede e falha o build.
    *   *Solução:* Utilizar `@SpringBootTest(properties = "eureka.client.enabled=false")` para isolar os testes.

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos
*   [Java 21+](https://jdk.java.net/21/)
*   [Maven](https://maven.apache.org/)
*   [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### Passo 1: Subir o Apache Kafka
Navegue até a raiz do projeto onde o arquivo `docker-compose.yml` está localizado e inicie o container:
```bash
docker-compose up -d
```

### Passo 2: Iniciar o Eureka Server
Navegue até o projeto do Registry (microservices) e inicie a aplicação:

```bash
mvn spring-boot:run
```
Acesse http://localhost:8761 no navegador. Você verá o painel do Eureka, mas sem nenhuma instância registrada ainda.

### Passo 3: Iniciar o Microsserviço Kafka
Em um novo terminal, navegue até o projeto do cliente (microservicesKafka) e inicie a aplicação:

```bash
mvn spring-boot:run
```
Após alguns segundos, atualize o painel do Eureka no navegador. Você verá o MICROSERVICESKAFKA registrado na lista de instâncias com o status UP.

No console do microsserviço, você poderá acompanhar os logs do produtor enviando dados e do consumidor recebendo a mensagem do tópico Meu-topico-teste.