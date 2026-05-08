# Análise do Projeto — teste-pratico-api

## Descrição do Projeto

Uma **API REST de gerenciamento de ocorrências** com evidências fotográficas. O sistema permite registrar ocorrências vinculadas a clientes e endereços, fazer upload de imagens como evidências, e controlar o ciclo de vida de cada ocorrência (ATIVA → FINALIZADA).

**Domínio central:** `Ocorrencia` → tem um `Cliente`, um `Endereco`, e várias `FotoOcorrencia` (salvas no MinIO).

---

## Stack Tecnológico

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.3.5 |
| Spring Data JPA + Hibernate | — |
| Spring Security + JWT (jjwt) | 0.12.5 |
| PostgreSQL | 17 |
| MinIO | 8.5.10 |
| Flyway | — |
| ModelMapper | 3.2.4 |
| Lombok | 1.18.30 |
| JUnit 5 + Mockito | — |
| Maven | 3.9.9 |

---

## Por que cada tecnologia foi escolhida

### Java 17 + Spring Boot 3.3.5
Java 17 é a versão LTS mais recente e amplamente adotada. Spring Boot 3.x exige Java 17 mínimo e oferece autoconfiguração de toda a stack (JPA, Security, Web) sem boilerplate de XML, acelerando o desenvolvimento.

### Spring Data JPA + Hibernate
Abstrai o acesso ao banco via `JpaRepository`, eliminando queries CRUD manuais. Para filtros dinâmicos (ex: buscar ocorrência por nome do cliente, cidade, data), foi necessário **Criteria API** — o JPA já inclui esta API, então não precisou de dependência extra.

### PostgreSQL 17
Banco relacional robusto, open-source e com suporte nativo a `IDENTITY` columns, índices compostos e schemas isolados (`ocorrencias`). A natureza relacional do domínio (Ocorrencia FK → Cliente FK → Endereço) justifica um banco relacional.

### Spring Security + JWT (jjwt 0.12.5)
A API precisa de autenticação stateless (sem sessão no servidor) para funcionar bem em containers e múltiplas instâncias. JWT permite que cada request carregue sua própria identidade no header. O **jjwt** é a biblioteca JWT mais madura do ecossistema Java. **BCrypt** é usado para hash de senhas por ser resistente a ataques de força bruta.

### MinIO
Armazenamento de objetos (S3-compatible) self-hosted para as evidências fotográficas. Escolhido porque:
- Imagens não devem ficar em banco relacional (performance e tamanho)
- Gera **URLs temporárias** (presigned URLs) para acesso seguro sem expor o storage diretamente
- Funciona com docker-compose, sem custo de cloud

### Flyway
Versionamento de schema de banco (V1 a V7). Garante que o banco esteja sempre no estado correto ao subir a aplicação, tanto em dev quanto em produção. Essencial para trabalho em equipe e deploys automáticos.

### Criteria API (JPA)
Necessária para as pesquisas dinâmicas com múltiplos filtros opcionais. JPQL estático não suporta condicionais. A classe `CriteriaQueryUtils` foi criada como utilitário genérico reutilizável entre os três repositórios.

### ModelMapper (STRICT mode)
Automatiza a conversão Entity → DTO e DTO → Entity, evitando código repetitivo de mapeamento campo a campo. O modo **STRICT** foi escolhido para evitar mapeamentos acidentais entre campos com nome parecido mas semântica diferente.

### Lombok
Elimina getters, setters, construtores e builders repetitivos nas entidades e DTOs. Como o projeto tem 5 entidades e ~12 DTOs, a redução de boilerplate é significativa.

### Docker + docker-compose
O docker-compose orquestra os 3 serviços (App, PostgreSQL, MinIO) com um único comando, garantindo ambiente reproduzível. O **Dockerfile multi-stage** separa o build (Maven + JDK) da imagem final (JRE apenas), reduzindo o tamanho da imagem de runtime.

### JUnit 5 + Mockito + MockMvc
JUnit 5 é o padrão atual de testes Java. Mockito mockeia dependências (Service, Repository) para testes unitários isolados. MockMvc testa os controllers sem subir o servidor completo, verificando status HTTP, serialização JSON e validações de entrada.

---

## Estrutura de Diretórios

```
teste-pratico-api/
├── src/
│   ├── main/
│   │   ├── java/br/com/teste_pratico_api/
│   │   │   ├── TestePraticoApiApplication.java
│   │   │   ├── api/              # Constantes de endpoints
│   │   │   ├── config/           # SecurityConfig, MinioConfig, ModelMapperConfig
│   │   │   ├── controller/       # AuthController, ClienteController, EnderecoController, OcorrenciaController
│   │   │   ├── domain/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/  # LoginRequestDTO, ClienteRequestDTO, ...
│   │   │   │   │   └── response/ # LoginResponseDTO, ClienteResponseDTO, ...
│   │   │   │   ├── entity/       # Cliente, Endereco, Ocorrencia, FotoOcorrencia, Usuario
│   │   │   │   └── enums/        # StatusOcorrencia (ATIVA, FINALIZADA)
│   │   │   ├── exception/        # GlobalExceptionHandler, exceções customizadas
│   │   │   ├── repository/
│   │   │   │   ├── custom/       # Interfaces Criteria API
│   │   │   │   ├── impl/         # Implementações Criteria API
│   │   │   │   └── filter/       # Objetos de filtro de busca
│   │   │   ├── security/         # JwtService, JwtAuthenticationFilter
│   │   │   ├── service/          # ClienteService, EnderecoService, OcorrenciaService, StorageService
│   │   │   └── util/             # CriteriaQueryUtils, MapperCustom, StringUtils
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/     # V1 a V7 (SQL)
│   └── test/                     # Testes unitários e de controller
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## Endpoints

### Auth — `/api/v1/auth`
| Método | Path | Descrição |
|---|---|---|
| POST | `/login` | Autenticação, retorna JWT |

### Clientes — `/api/v1/clientes`
| Método | Path | Descrição |
|---|---|---|
| POST | `/` | Criar cliente |
| GET | `/` | Listar com filtros e paginação |
| GET | `/{id}` | Buscar por ID |
| PUT | `/{id}` | Atualizar cliente |
| DELETE | `/{id}` | Excluir cliente |

### Endereços — `/api/v1/enderecos`
| Método | Path | Descrição |
|---|---|---|
| POST | `/` | Criar endereço |
| GET | `/` | Listar com filtros e paginação |
| GET | `/{id}` | Buscar por ID |
| PUT | `/{id}` | Atualizar endereço |
| DELETE | `/{id}` | Excluir endereço |

### Ocorrências — `/api/v1/ocorrencias`
| Método | Path | Descrição |
|---|---|---|
| POST | `/` | Criar ocorrência simples |
| POST | `/cadastro-completo` | Criar com upload de evidências (multipart) |
| POST | `/{id}/evidencias` | Adicionar evidências a ocorrência existente |
| GET | `/` | Listar com filtros e paginação |
| GET | `/{id}` | Buscar por ID |
| PUT | `/{id}` | Atualizar ocorrência |
| DELETE | `/{id}` | Excluir ocorrência |
| PATCH | `/{id}/finalizar` | Finalizar ocorrência (irreversível) |

---

## Arquitetura

```
Request
  └─> JwtAuthenticationFilter  (valida Bearer token)
        └─> Controller          (recebe DTO, chama Service)
              └─> Service       (lógica de negócio, @Transactional)
                    ├─> Repository (Criteria API → PostgreSQL)
                    └─> StorageService (upload/link → MinIO)
  └─> Response (DTO serializado em JSON)

GlobalExceptionHandler (@RestControllerAdvice)
  └─> Captura exceções → ErrorTemplate padronizado
```

**Princípios:**
- Stateless (JWT sem sessão no servidor)
- Camadas separadas (Controller → Service → Repository)
- DTOs isolam o domínio da API pública
- Exception handling centralizado
- Ordenação segura via whitelist de campos permitidos
- Transações em operações críticas

---

## Credenciais padrão (desenvolvimento)

| Recurso | Valor |
|---|---|
| Login API | `admin` / `admin123` |
| PostgreSQL | `postgres` / `postgres` |
| MinIO | `minioadmin` / `minioadmin` |
| JWT expiration | 30 minutos |

---

## Subir o ambiente

```bash
docker-compose up --build
```

Serviços disponíveis:
- API: `http://localhost:8080`
- MinIO Console: `http://localhost:9001`
- PostgreSQL: `localhost:5432`