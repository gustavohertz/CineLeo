# 🎭 CineLeo App Terminal

Aplicação cliente via terminal do ecossistema **CineLeo**, desenvolvida em **Java 21** com **Spring Boot**.

O projeto funciona como um **front-end de linha de comando (CLI)**, consumindo todos os microsserviços através do **API Gateway**, simulando o comportamento de uma aplicação real (Web, Mobile ou Desktop).

---

## 📋 Visão Geral

O App Terminal é responsável por permitir que usuários interajam com o ecossistema CineLeo através de menus interativos no terminal.

A aplicação se comunica exclusivamente com o **API Gateway**, sem acesso direto aos microsserviços internos.

---

## ✨ Funcionalidades

### 👤 Usuários

- Cadastro de novos usuários;
- Login com autenticação JWT;
- Consulta de informações da conta;
- Gerenciamento de sessão autenticada.

### 🎬 Filmes

- Listagem de filmes em cartaz;
- Consulta de detalhes dos filmes;
- Visualização de sessões disponíveis.

### 🎟 Reservas

- Seleção de sessões;
- Reserva de ingressos;
- Consulta de ingressos adquiridos.

### 💳 Pagamentos

- Simulação de pagamento;
- Confirmação de compra;
- Integração com o serviço de pagamentos.

---

## 🏗 Arquitetura

O App segue uma arquitetura simples baseada em consumo de APIs REST.

```text
Cliente (App Terminal)
        │
        ▼
API Gateway (9999)
        │
        ├── Usuarios Service (8083)
        ├── Eventos Service (8082)
        ├── Pagamento Service (5000)
        └── Notification Service (8000)
```

### Importante

O App **nunca se comunica diretamente** com os microsserviços.

Toda comunicação ocorre através do:

```text
http://localhost:9999
```

(API Gateway)

Isso reproduz exatamente o comportamento de aplicações reais em produção.

---

## ⚙️ Tecnologias Utilizadas

### Linguagem

- Java 21

### Framework

- Spring Boot 3

### Comunicação

- RestTemplate
- API Gateway

### Build

- Maven 3.8+

---

## 📦 Pré-requisitos

Antes de executar o App, certifique-se de que os seguintes serviços estejam em execução:

| Serviço | Porta |
|----------|----------|
| Eureka Server | 8761 |
| API Gateway | 9999 |
| Usuarios Service | 8083 |
| Eventos Service | 8082 |
| Pagamento Service | 5000 |
| Notification Service *(opcional)* | 8000 |
| Kafka *(opcional)* | 9092 |

---

## 🔧 Configuração

Arquivo:

```text
src/main/resources/application.properties
```

Configuração padrão:

```properties
gateway.url=http://localhost:9999
```

Caso o Gateway esteja em outra porta ou endereço, altere essa propriedade.

---

## ▶️ Como Executar

### 1. Clonar o projeto

```bash
git clone <repositorio>
```

---

### 2. Entrar na pasta do projeto

```bash
cd App
```

---

### 3. Executar a aplicação

```bash
mvn clean spring-boot:run
```

---

### 4. Iniciar utilização

Após a inicialização será exibido o menu principal:

```text
=================================
      BEM-VINDO AO CINELEO
=================================

1 - Ver catálogo de filmes
2 - Meus ingressos
3 - Minha conta
4 - Fazer login
5 - Criar conta
0 - Sair
```

---

## 🔄 Fluxo de Utilização

### Cadastro

```text
Menu Principal
      │
      ▼
Criar Conta
      │
      ▼
Preencher Dados
      │
      ▼
Conta Criada
```

---

### Login

```text
Menu Principal
      │
      ▼
Login
      │
      ▼
Validação no Usuarios Service
      │
      ▼
Token JWT Recebido
```

---

### Compra de Ingresso

```text
Catálogo de Filmes
      │
      ▼
Escolha do Filme
      │
      ▼
Escolha da Sessão
      │
      ▼
Reserva
      │
      ▼
Pagamento
      │
      ▼
Confirmação
      │
      ▼
Ingresso Disponível
```

---

## 🎬 Fluxo Completo do Cliente

```text
Início
   │
   ▼
Criar Conta
   │
   ▼
Login
   │
   ▼
Visualizar Filmes
   │
   ▼
Selecionar Sessão
   │
   ▼
Reservar Ingresso
   │
   ▼
Efetuar Pagamento
   │
   ▼
Receber Confirmação
   │
   ▼
Consultar Ingressos
   │
   ▼
Fim
```

---

## 🚀 Como Executar Todo o Ecossistema

Execute os serviços na seguinte ordem:

### 1. Eureka Server

```text
http://localhost:8761
```

---

### 2. Usuarios Service

```text
porta 8083
```

---

### 3. Pagamento Service

```text
porta 5000
```

---

### 4. Eventos Service

```text
porta 8082
```

---

### 5. API Gateway

```text
porta 9999
```

---

### 6. Notification Service (Opcional)

```text
porta 8000
```

---

### 7. Apache Kafka (Opcional)

```text
porta 9092
```

---

### 8. Executar o App

```bash
cd App

mvn spring-boot:run
```

---

## 📁 Estrutura do Projeto

```text
App/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── cineleo/
    │   │           └── app/
    │   │               ├── AppApplication.java
    │   │               ├── client/
    │   │               ├── config/
    │   │               ├── dto/
    │   │               ├── service/
    │   │               └── cli/
    │   │
    │   └── resources/
    │       └── application.properties
    │
    └── test/
        └── java/
```

---

## 🔐 Autenticação

Após o login, o sistema recebe um token JWT do serviço de usuários.

Esse token é automaticamente enviado para os endpoints protegidos através do Gateway.

Exemplo:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 📡 Integração com Microsserviços

### Usuarios Service

Responsável por:

- Cadastro de usuários;
- Login;
- Autenticação;
- Consulta de dados da conta.

---

### Eventos Service

Responsável por:

- Filmes;
- Salas;
- Sessões;
- Reservas.

---

### Pagamento Service

Responsável por:

- Processamento de pagamentos;
- Aprovação ou recusa de transações.

---

### Notification Service

Responsável por:

- Notificações;
- Mensagens;
- Envio de e-mails.

---

## 🧪 Testes

Executar testes:

```bash
mvn test
```

Gerar pacote da aplicação:

```bash
mvn clean package
```

---

## 🧠 Resumo da Arquitetura

```text
┌─────────────────────────────┐
│      App Terminal CLI       │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│        API Gateway          │
│         Porta 9999          │
└──────────────┬──────────────┘
               │
     ┌─────────┼─────────┐
     │         │         │
     ▼         ▼         ▼

Usuarios    Eventos   Pagamento
 8083        8082        5000

               │
               ▼

        Notification
            8000

               │
               ▼

            Kafka
            9092
```

O App Terminal atua como a camada de apresentação do ecossistema CineLeo, consumindo os serviços através do Gateway e reproduzindo o comportamento de um cliente real.

---

## 📄 Licença

Projeto de uso interno do ecossistema **CineLeo**.

Todos os direitos reservados.