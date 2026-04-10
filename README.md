# 🚀 Teste Prático API - Gestão de Ocorrências

API REST desenvolvida com **Spring Boot** para gerenciamento de ocorrências, incluindo cadastro, consulta, upload de evidências (MinIO) e autenticação via JWT.

---

# 📌 📦 Tecnologias Utilizadas

* Java 17
* Spring Boot
* Spring Data JPA
* Spring Security + JWT
* PostgreSQL
* Flyway (migrations)
* MinIO (armazenamento de arquivos)
* JUnit 5 + Mockito (testes)
* ModelMapper

---

# ⚙️ 🚀 Como Executar o Projeto

## 🔧 Pré-requisitos

* Java 17+
* Maven
* Docker (recomendado)

---

## 🐳 Subindo com Docker

```bash
docker-compose up -d
```

Serviços disponíveis:

| Serviço    | URL                   |
| ---------- | --------------------- |
| API        | http://localhost:8080 |
| MinIO      | http://localhost:9001 |
| PostgreSQL | localhost:5432        |

**MinIO**

* usuário: `minioadmin`
* senha: `minioadmin`

---

## ▶️ Rodando manualmente

```bash
mvn clean install
mvn spring-boot:run
```

---

# 🔐 Autenticação

A API utiliza JWT.

## Login

**POST** `/api/v1/auth/login`

### Entrada

```json
{
  "login": "admin",
  "senha": "admin123"
}
```

### Saída

```json
{
  "token": "jwt-token",
  "tipo": "Bearer",
  "expiraEm": 1800
}
```

### Uso

Enviar no header:

```
Authorization: Bearer {token}
```

---

# 🔌 Endpoints

## 👤 Cliente

### POST `/api/v1/clientes`

Cria cliente

### GET `/api/v1/clientes`

Lista com paginação e filtros

### GET `/api/v1/clientes/{id}`

Busca por ID

### PUT `/api/v1/clientes/{id}`

Atualiza cliente

### DELETE `/api/v1/clientes/{id}`

Remove cliente

---

## 📍 Endereço

### POST `/api/v1/enderecos`

Cria endereço

### GET `/api/v1/enderecos`

Lista endereços com paginação e filtros

### GET `/api/v1/enderecos/{id}`

Busca por ID

### PUT `/api/v1/enderecos/{id}`

Atualiza endereço

### DELETE `/api/v1/enderecos/{id}`

Remove endereço

---

## 🚨 Ocorrência

### POST `/api/v1/ocorrencias`

Criação simples

---

### POST `/api/v1/ocorrencias/cadastro-completo`

Cadastro com evidências

**multipart/form-data**

* `request` → JSON
* `files` → imagens

---

### POST `/api/v1/ocorrencias/{id}/evidencias`

Upload de evidências

---

### GET `/api/v1/ocorrencias`

Lista com filtros:

* nome do cliente
* CPF
* data
* cidade

Ordenação:

* data
* cidade

---

### GET `/api/v1/ocorrencias/{id}`

Busca por ID

---

### PATCH `/api/v1/ocorrencias/{id}/finalizar`

Finaliza ocorrência

⚠️ Após finalizada, não pode ser alterada

---

# ⚠️ Tratamento de Erros

## Estrutura padrão

```json
{
  "timestamp": "2026-04-10T14:00:00Z",
  "status": 400,
  "error": "Erro na requisição",
  "message": "Descrição do erro",
  "path": "/endpoint"
}
```

## Principais erros

| Código | Descrição                |
| ------ | ------------------------ |
| 400    | Dados inválidos          |
| 401    | Não autenticado          |
| 403    | Sem permissão            |
| 404    | Recurso não encontrado   |
| 409    | Regra de negócio violada |
| 500    | Erro interno             |

---

# 🧪 Testes

Executar:

```bash
mvn test
```

## Cobertura implementada

* Controllers (MockMvc)
* Service (regras de negócio)
* Security (JWT)
* Upload de arquivos (mockado)

---

# 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas:

```
controller → service → repository → database
```

Separação clara de responsabilidades:

* Controller → entrada HTTP
* Service → regras de negócio
* Repository → acesso a dados
* DTOs → comunicação externa
* Mapper → transformação de dados

---

# ⚡ Escalabilidade

A solução foi pensada para escalar:

* uso de **JWT stateless**
* armazenamento externo de arquivos (MinIO)
* separação de camadas
* uso de paginação e filtros
* banco relacional com índices
* Flyway para versionamento

Possível evolução:

* cache (Redis)
* mensageria (RabbitMQ/Kafka)

---

# 📌 Decisões e Priorização

## ✔ Implementado

* CRUD completo (Cliente, Endereço, Ocorrência)
* Upload de evidências (MinIO)
* Endpoint de cadastro completo
* Filtros e ordenação
* Autenticação JWT
* Testes unitários e de controller
* Tratamento global de exceções

---

## ⚠️ Não implementado (por priorização)

* Testes de integração com banco real (Testcontainers)
* Monitoramento (Actuator + métricas)

---

## 🎯 Motivo da priorização

Foi priorizado:

1. **Regras de negócio principais**
2. **Funcionalidades obrigatórias**
3. **Qualidade do código e testes**
4. **Arquitetura limpa e organizada**

Itens não implementados foram considerados melhorias futuras e não essenciais para o funcionamento principal da aplicação.

---

# 📬 Considerações finais

Projeto desenvolvido com foco em:

* boas práticas
* organização de código
* testabilidade
* clareza de arquitetura
* Clean Code
* Escalabilidade
* Legibilidade

---

💡 Projeto pronto para evolução em ambiente real.
