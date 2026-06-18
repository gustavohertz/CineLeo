# CineLeo - Fluxo de Compra de Ingressos

## Fluxo Principal

```text
INÍCIO
   │
   ▼
Boas-vindas ao CineLeo
   │
   ▼
Digite a opção desejada:

1 - Ver Catálogo de Filmes
2 - Meus Ingressos
3 - Minha Conta
   │
   ├────────► [1] Ver Catálogo de Filmes
   │
   ├────────► [2] Meus Ingressos
   │
   └────────► [3] Minha Conta
```

---

# 1. Ver Catálogo de Filmes

## Fluxo

```text
Usuário seleciona opção 1
           │
           ▼
Sistema exibe catálogo de filmes
           │
           ▼
Usuário escolhe um filme
           │
           ▼
Sistema exibe horários disponíveis
           │
           ▼
Usuário escolhe um horário
           │
           ▼
Usuário está logado?
      │              │
     NÃO            SIM
      │              │
      ▼              ▼
Login/Cadastro   Seleção de Assentos
      │              │
      └──────┬───────┘
             ▼
      Seleção de Assentos
             │
             ▼
Usuário seleciona até 5 assentos
             │
             ▼
Confirmar seleção?
             │
             ▼
Tela de Pagamento
             │
             ▼
Escolher tipo:
1 - Inteira
2 - Meia
             │
             ▼
Confirmar pagamento?
        SIM / NÃO
             │
             ▼
Processar pagamento
             │
             ▼
Enviar notificação:
- Terminal
- E-mail
             │
             ▼
Retornar ao Menu Principal
```

---

# 2. Meus Ingressos

## Fluxo

```text
Usuário seleciona opção 2
           │
           ▼
Usuário está logado?
      │              │
     NÃO            SIM
      │              │
      ▼              ▼
Login         Exibir ingressos
      │              │
      └──────┬───────┘
             ▼
Exibir:
- Filme
- Data
- Horário
- Assentos
- Valor Pago
             │
             ▼
Voltar ao Menu Principal
```

---

# 3. Minha Conta

## Fluxo

```text
Usuário seleciona opção 3
           │
           ▼
Usuário está logado?
      │              │
     NÃO            SIM
      │              │
      ▼              ▼
Login       Exibir dados da conta
      │              │
      └──────┬───────┘
             ▼
Exibir:
- Nome
- CPF
- E-mail
- Data de Cadastro
- Histórico de Compras
             │
             ▼
Voltar ao Menu Principal
```

---

# Regras de Negócio

## Compra de Ingressos

* O usuário pode selecionar até 5 assentos por compra.
* Um assento já ocupado não pode ser selecionado.
* O pagamento é realizado utilizando dados simulados (mock).
* Após a conclusão da compra, o ingresso é armazenado no histórico do usuário.

## Notificações

Após a tentativa de pagamento:

### Sucesso

* Notificação exibida no terminal.
* E-mail enviado ao cliente.
* Ingresso disponibilizado em "Meus Ingressos".

### Falha

* Notificação exibida no terminal.
* E-mail enviado informando a falha.
* Usuário retorna ao menu principal.

## Autenticação

* Usuários não autenticados devem realizar login ou cadastro antes de concluir uma compra.
* O acesso a "Meus Ingressos" e "Minha Conta" exige autenticação.

---

# Fluxo Resumido

```text
Menu Principal
      │
      ├──► Ver Catálogo
      │        │
      │        ▼
      │   Escolher Filme
      │        │
      │        ▼
      │   Escolher Horário
      │        │
      │        ▼
      │      Login
      │        │
      │        ▼
      │   Escolher Assentos
      │        │
      │        ▼
      │      Pagamento
      │        │
      │        ▼
      │   Notificação
      │        │
      │        ▼
      │   Menu Principal
      │
      ├──► Meus Ingressos
      │        │
      │        ▼
      │   Visualizar Ingressos
      │        │
      │        ▼
      │   Menu Principal
      │
      └──► Minha Conta
               │
               ▼
         Visualizar Dados
               │
               ▼
         Menu Principal
```
