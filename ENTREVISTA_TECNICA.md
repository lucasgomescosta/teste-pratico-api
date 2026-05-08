# Perguntas e Respostas — Entrevista Técnica

---

## Spring Boot & Arquitetura

---

**Por que você escolheu arquitetura em camadas ao invés de hexagonal ou clean architecture?**

A arquitetura em camadas (Controller → Service → Repository) é a mais natural para projetos Spring Boot e é amplamente conhecida por qualquer desenvolvedor Java. Para um teste prático com escopo definido, ela entrega a separação de responsabilidades necessária sem a complexidade adicional de portas e adaptadores. Hexagonal ou Clean Architecture fazem mais sentido em sistemas grandes com múltiplos pontos de entrada (REST, CLI, mensageria) ou onde o domínio precisa ser completamente isolado de frameworks.

---

**O que é `open-in-view=false` no JPA e por que você desativou?**

Por padrão, o Spring Boot mantém a sessão JPA aberta durante toda a requisição HTTP, até a resposta ser enviada. Isso permite carregar relacionamentos lazy na camada de view/controller, mas tem dois problemas sérios: executa queries silenciosas fora da camada de serviço (difícil de rastrear) e mantém conexão com o banco por mais tempo do que o necessário. Com `open-in-view=false`, a sessão JPA é fechada ao sair do Service, forçando que todos os dados necessários sejam carregados explicitamente dentro da transação.

---

**Como funciona o ciclo de vida de um request nessa API?**

```
1. Request HTTP chega ao Tomcat
2. JwtAuthenticationFilter intercepta → extrai token do header Authorization
3. Valida token → popula SecurityContextHolder com o usuário autenticado
4. Spring MVC roteia para o Controller correto
5. Controller deserializa o JSON para DTO de request
6. Controller chama o Service
7. Service executa lógica de negócio, chama Repository ou StorageService
8. Repository executa query no PostgreSQL via Criteria API
9. Resultado volta como entidade → Service mapeia para DTO de response
10. Controller retorna ResponseEntity com o DTO
11. Jackson serializa o DTO para JSON
12. Response HTTP é enviada ao cliente
```

Se qualquer exceção for lançada nos passos 6-9, o `GlobalExceptionHandler` intercepta e retorna um `ErrorTemplate` padronizado.

---

**O que acontece se o Service lançar uma exceção dentro de um método `@Transactional`?**

Depende do tipo da exceção. Por padrão, `@Transactional` faz rollback automático apenas para `RuntimeException` e `Error`. Para exceções checked (`Exception`), o rollback não acontece a menos que você configure `@Transactional(rollbackFor = Exception.class)`. No projeto, como `BusinessException` e as exceções de NotFound estendem `RuntimeException`, o rollback ocorre automaticamente. Isso é importante no `cadastroCompleto`, onde se o upload para o MinIO falhar, a inserção da ocorrência no banco também é revertida.

---

**Por que usar DTOs ao invés de expor as entidades diretamente?**

Três motivos principais:
1. **Segurança**: entidades podem ter campos sensíveis (senha, campos internos) que não devem trafegar na API
2. **Desacoplamento**: mudanças no schema do banco não quebram o contrato da API
3. **Forma dos dados**: o response de ocorrência retorna `nmeCliente` e `nmeCidade` (desnormalizados), o que seria impossível com a entidade pura sem expor todo o grafo de objetos

---

## Spring Security + JWT

---

**Como funciona o fluxo de autenticação JWT nessa API?**

```
1. Cliente faz POST /api/v1/auth/login com { login, senha }
2. AuthController chama AuthenticationManager.authenticate()
3. Spring Security chama CustomUserDetailsService.loadUserByUsername()
4. Busca usuário no banco, compara senha com BCrypt
5. Se válido, JwtService.gerarToken() cria um JWT assinado com HMAC-SHA256
6. Retorna { token, tipo: "Bearer", expiraEm: 1800000 }
7. Nas próximas requisições, cliente envia: Authorization: Bearer {token}
8. JwtAuthenticationFilter extrai o token, valida assinatura e expiração
9. Popula SecurityContextHolder → request prossegue autenticado
```

---

**Onde o token é validado? Quem chama o `JwtAuthenticationFilter`?**

O `JwtAuthenticationFilter` é um filtro registrado na cadeia de filtros do Spring Security via `SecurityConfig`. O próprio Servlet container (Tomcat) executa a cadeia de filtros antes de qualquer Controller. O filtro estende `OncePerRequestFilter`, garantindo que seja executado exatamente uma vez por request, mesmo em forwards internos.

---

**O que é `OncePerRequestFilter` e por que é usado?**

É uma classe base do Spring que garante que o filtro execute uma única vez por request HTTP. Sem ela, em cenários com forward interno (ex: tratamento de erro que faz forward para outro endpoint), o filtro poderia ser executado duas vezes, causando dupla validação ou duplo registro no SecurityContext.

---

**O que acontece se o token expirar?**

O `JwtService.tokenValido()` verifica a expiração ao fazer o parse do token via jjwt. Se expirado, lança `ExpiredJwtException`. O `JwtAuthenticationFilter` captura essa exceção, não popula o `SecurityContextHolder`, e a requisição segue sem autenticação. O Spring Security então aciona o `CustomAuthenticationEntryPoint`, que retorna **401 Unauthorized** com o `ErrorTemplate` padronizado.

---

**Por que JWT stateless é melhor que sessão para APIs containerizadas?**

Com sessão, o estado fica no servidor (memória ou Redis). Se você tem 3 instâncias da API rodando, a requisição precisa sempre chegar à mesma instância (sticky session) ou você precisa de sessão distribuída (Redis). Com JWT, o estado está no próprio token. Qualquer instância consegue validar o token apenas com a chave secreta, sem consultar nenhum armazenamento externo. Isso facilita escalar horizontalmente sem configuração adicional.

---

**O que o `CustomAuthenticationEntryPoint` faz e quando é acionado?**

É acionado quando uma requisição chega em endpoint protegido sem autenticação válida (token ausente, inválido ou expirado). Implementa `AuthenticationEntryPoint` do Spring Security. Sem ele, o Spring retornaria uma página HTML de erro padrão. Com ele, a resposta é sempre JSON no formato `ErrorTemplate`, consistente com o restante da API.

---

**Por que BCrypt para senhas? O que diferencia de MD5 ou SHA?**

MD5 e SHA são algoritmos de hash rápidos, projetados para integridade de dados, não para senhas. Um atacante com GPU pode testar bilhões de combinações por segundo contra um hash MD5. BCrypt tem um **fator de custo** configurável que torna o hash deliberadamente lento (ex: 10 rounds = ~100ms por hash). Mesmo com hardware potente, testar um dicionário inteiro levaria anos. Além disso, BCrypt incorpora um **salt** aleatório em cada hash, então duas senhas iguais geram hashes diferentes, eliminando ataques por rainbow table.

---

## JPA / Hibernate / Criteria API

---

**Por que você usou Criteria API ao invés de JPQL ou `@Query`?**

Os filtros de busca são todos opcionais. Com JPQL ou `@Query`, você precisaria de uma query diferente para cada combinação de filtros, ou usar concatenação de strings (perigoso, propenso a erros). A Criteria API permite construir a query programaticamente, adicionando predicates apenas quando o filtro está preenchido. O `CriteriaQueryUtils` encapsula isso com métodos como `addLikeIfHasText` e `addEqualIfNotNull`, mantendo o código limpo e reutilizável.

---

**O que é N+1 problem? Esse projeto tem? Como você mitigou?**

O problema N+1 acontece quando você busca uma lista de N entidades e depois executa 1 query adicional para cada entidade para carregar um relacionamento. Exemplo: buscar 20 ocorrências e depois fazer 20 queries para carregar o cliente de cada uma = 21 queries no total.

No projeto, o `OcorrenciaRepositoryImpl` usa `fetch` na Criteria API para carregar cliente, endereço e fotos em uma única query com JOIN. Isso é equivalente ao `JOIN FETCH` do JPQL e elimina o N+1.

```java
root.fetch("cliente", JoinType.LEFT);
root.fetch("endereco", JoinType.LEFT);
root.fetch("fotos", JoinType.LEFT);
```

---

**O que é `CascadeType.ALL` na relação `Ocorrencia → FotoOcorrencia`? Quais riscos?**

`CascadeType.ALL` propaga todas as operações JPA (persist, merge, remove, refresh, detach) da entidade pai para os filhos. Significa que ao salvar uma `Ocorrencia`, as `FotoOcorrencia` associadas são salvas automaticamente, e ao deletar a ocorrência, as fotos são deletadas em cascata.

O risco é deletar dados acidentalmente. Se você busca uma ocorrência, limpa a lista de fotos e chama `save()`, todas as fotos são deletadas. Outro risco é o `orphanRemoval`: se ativado junto, remover uma foto da lista da ocorrência a deleta do banco automaticamente, o que pode ser surpreendente.

---

**Por que `ddl-auto=none`? Qual o risco de usar `update` em produção?**

Com `ddl-auto=update`, o Hibernate analisa as entidades e tenta alterar o schema do banco para corresponder ao modelo Java. O problema é que ele só **adiciona** coisas (novas colunas, tabelas), nunca remove. Isso leva a schema drift silencioso ao longo do tempo. Além disso, não há controle sobre quando e como as alterações acontecem, e em ambientes com múltiplas instâncias pode causar condições de corrida. Com `none` e Flyway, cada alteração de schema é uma migration versionada, revisada, testada e aplicada de forma controlada.

---

**O que é lazy vs eager loading? Onde cada um foi usado?**

- **Lazy**: o relacionamento só é carregado quando você acessa o campo. É o padrão para `@OneToMany` e `@ManyToMany`.
- **Eager**: o relacionamento é carregado junto com a entidade pai, sempre. É o padrão para `@ManyToOne` e `@OneToOne`.

No projeto, `Cliente` e `Endereco` em `Ocorrencia` são `@ManyToOne` (eager por padrão), e `fotos` é `@OneToMany` (lazy por padrão). Para as queries de listagem, o `OcorrenciaRepositoryImpl` usa `fetch` explícito para carregar tudo em uma única query, evitando lazy loading fora da transação (o que causaria `LazyInitializationException` com `open-in-view=false`).

---

**O que é `@Transactional` no `OcorrenciaService`? O que acontece sem ele no cadastro completo?**

`@Transactional` garante que todas as operações de banco dentro do método aconteçam em uma única transação atômica. No `cadastroCompleto`, o fluxo é:
1. Salva a `Ocorrencia`
2. Para cada arquivo: faz upload no MinIO, cria `FotoOcorrencia`, salva no banco

Sem `@Transactional`, se o terceiro arquivo falhar no upload, as duas primeiras fotos já teriam sido salvas no banco mas o MinIO estaria inconsistente. Com `@Transactional`, se qualquer passo lançar `RuntimeException`, todo o banco é revertido — embora o MinIO não faça parte da transação JPA (ele é um sistema externo), então os arquivos já enviados ao MinIO não são removidos automaticamente. Isso é uma limitação conhecida que exigiria um mecanismo de compensação para ser totalmente resolvida.

---

## PostgreSQL & SQL

---

**Por que usar schema isolado (`ocorrencias`) ao invés do schema público?**

O schema `public` do PostgreSQL é compartilhado por padrão com qualquer conexão ao banco. Usar um schema dedicado (`ocorrencias`) isola as tabelas da aplicação, facilita gerenciar permissões (um usuário de aplicação com acesso apenas ao schema `ocorrencias`), e permite que múltiplas aplicações coexistam no mesmo banco sem conflito de nomes de tabela.

---

**O que é `GENERATED BY DEFAULT AS IDENTITY`? Diferença para `SERIAL`?**

Ambos auto-incrementam o ID, mas `IDENTITY` é o padrão SQL:2003, enquanto `SERIAL` é uma conveniência histórica do PostgreSQL. A diferença prática: `IDENTITY` permite que você insira um valor explícito (`BY DEFAULT`) ou proíba totalmente (`ALWAYS`). `SERIAL` cria implicitamente uma sequence e um default, o que pode causar surpresas ao fazer dump/restore ou ao resetar sequences. `IDENTITY` é a forma recomendada no PostgreSQL moderno.

---

**Por que os índices foram criados nessas colunas específicas?**

Índices aceleram buscas, mas têm custo em insert/update. As colunas indexadas foram escolhidas com base nos padrões de consulta:
- `nro_cpf`: campo único e usado em filtros de busca — índice UNIQUE já o cobre
- `dta_ocorrencia`: usada em filtros de range de data e ordenação
- `sta_ocorrencia`: filtros por status (ATIVA/FINALIZADA)
- `nme_cidade`, `nme_bairro`: usados em filtros LIKE e ordenação
- FKs (`cod_cliente`, `cod_endereco`): o PostgreSQL não cria índice automático em FKs, então foram criados manualmente para acelerar JOINs

---

**Como você garantiria que dois requests simultâneos não finalizem a mesma ocorrência duas vezes?**

Com **locking otimista** ou **pessimista**:

- **Otimista** (`@Version`): adiciona uma coluna `version` na entidade. Se dois requests leem a mesma versão e ambos tentam salvar, o segundo falha com `OptimisticLockException`. Bom para baixa contenção.
- **Pessimista** (`SELECT FOR UPDATE`): o primeiro request que ler a ocorrência bloqueia a linha no banco. O segundo espera. Garante consistência mas pode causar deadlock se mal usado.

Para o `finalizar`, a solução mais simples seria: dentro da transação, verificar `if (ocorrencia.getStatus() == FINALIZADA) throw BusinessException`. Com o lock pessimista, apenas um dos requests conseguiria prosseguir.

---

## Flyway

---

**O que acontece se você modificar uma migration já aplicada em produção?**

O Flyway calcula um checksum de cada arquivo SQL quando aplica. Se você modificar um arquivo já aplicado, na próxima inicialização o Flyway detecta que o checksum mudou e lança `FlywayException`, impedindo a aplicação de subir. Isso é intencional: migrations são imutáveis. A forma correta de corrigir é criar uma nova migration (ex: V8) que desfaz ou corrige o que foi feito na versão anterior.

---

**Para que serve `baseline-on-migrate`?**

Quando você adiciona o Flyway a um banco que já existe (sem histórico de migrations), o Flyway não sabe o estado atual do banco e recusa executar. `baseline-on-migrate=true` faz o Flyway marcar o estado atual do banco como a linha de base (versão 1), e a partir daí aplica apenas as migrations novas. É usado para onboarding do Flyway em projetos legados.

---

**Qual a diferença entre `out-of-order=true` e o comportamento padrão?**

Por padrão, o Flyway exige que as migrations sejam aplicadas em ordem crescente de versão. Se a V5 já foi aplicada e você tenta aplicar a V3 (que chegou de outra branch), o Flyway rejeita. Com `out-of-order=true`, ele aplica a V3 mesmo que versões posteriores já existam no histórico. Útil em times com múltiplas branches criando migrations em paralelo, mas requer cuidado para que as migrations não sejam dependentes de ordem.

---

**Como você faria rollback de uma migration problemática?**

O Flyway Community (open-source) não tem rollback nativo. As opções são:
1. Criar uma migration de rollback manual (ex: `V8__rollback_V7.sql`)
2. Restaurar um backup do banco (mais confiável para produção)
3. Usar o Flyway Teams (pago) que tem suporte a `undo` migrations

Por isso, antes de aplicar em produção, sempre teste a migration em ambiente de staging com dados reais, e mantenha backups automatizados.

---

## MinIO / Storage

---

**Por que não salvar as imagens no banco (BLOB)?**

Três razões principais:
1. **Performance**: arquivos binários grandes inflam o banco, tornam backups lentos e aumentam I/O nas queries
2. **Escalabilidade**: bancos relacionais não são otimizados para streaming de arquivos grandes; object storage sim
3. **Custo**: armazenamento em object storage (MinIO, S3) é ordens de magnitude mais barato que storage de banco de dados

O banco guarda apenas o **path** do objeto no MinIO e o **hash SHA-256**, que são strings leves.

---

**O que são presigned URLs? Por que gerar link temporário?**

Uma presigned URL é uma URL do MinIO que contém credenciais de acesso embutidas e tem validade configurada (aqui: 1 dia). Permite que o cliente acesse diretamente o arquivo no MinIO sem passar pela API e sem que o bucket precise ser público.

Alternativas e seus problemas:
- **Bucket público**: qualquer pessoa com o link acessa, sem controle
- **Proxy pela API**: a API vira intermediária de download de arquivos binários grandes, consumindo memória e banda desnecessariamente

Com presigned URL, a API retorna apenas a URL temporária e o cliente baixa direto do MinIO.

---

**O que acontece se o upload para o MinIO falhar depois de salvar a ocorrência no banco?**

Este é um problema de **consistência distribuída**. O banco e o MinIO são dois sistemas independentes, então não existe transação que abranja os dois.

No projeto atual, o `@Transactional` garante rollback do banco se qualquer operação lançar exceção, incluindo a `StorageException`. Então se o MinIO falhar, o banco faz rollback. O problema inverso (MinIO salva mas banco falha) deixaria arquivos órfãos no MinIO — o banco reverteu mas o arquivo foi enviado.

Para resolver completamente, as soluções são:
- **Outbox pattern**: gravar a intenção de upload no banco transacionalmente, e processar o upload de forma assíncrona
- **Cleanup job**: tarefa periódica que remove do MinIO arquivos sem referência no banco
- **Two-phase commit**: complexo e raramente justificado

---

**O hash SHA-256 da foto serve para quê?**

Serve como **impressão digital** do arquivo:
1. **Detecção de duplicatas**: se dois uploads têm o mesmo hash, é o mesmo arquivo
2. **Integridade**: ao baixar o arquivo, você pode recalcular o hash e comparar para verificar que não foi corrompido ou adulterado
3. **Auditoria**: em contexto de ocorrências (possivelmente legais/policiais), o hash comprova que a evidência não foi alterada após o registro

---

## Testes

---

**Qual a diferença entre os testes de controller e os testes de service?**

- **Testes de Service** (`OcorrenciaServiceTest`): testam a lógica de negócio pura. Mockam o repository e o storage. Verificam se regras como "não pode finalizar ocorrência já finalizada" ou "não pode excluir ocorrência FINALIZADA" funcionam corretamente. Não sobem contexto Spring.

- **Testes de Controller** (`OcorrenciaControllerTest`): usam `MockMvc` para simular requests HTTP. Verificam status code (200, 400, 404), serialização/deserialização JSON, validações de entrada (`@Valid`), e se o controller chama o service com os parâmetros corretos. O service é mockado, então não testam lógica de negócio.

---

**Por que mockar o `StorageService` nos testes?**

O `StorageService` depende de uma instância real do MinIO rodando. Nos testes unitários, não há MinIO disponível (e não deveria haver — testes unitários devem ser rápidos e sem infraestrutura). Mockando com Mockito, você define o comportamento esperado (`when(storageService.uploadFile(any())).thenReturn("path/foto.jpg")`) e testa a lógica do service de forma isolada e determinística.

---

**O que o `MockMvc` testa que um teste unitário de Service não testa?**

- Deserialização do JSON de entrada (campos obrigatórios, tipos)
- Validações do Bean Validation (`@NotNull`, `@Size`) que são processadas pelo Spring antes de chegar ao Service
- Status HTTP correto para cada cenário
- Formato do JSON de resposta
- Headers de resposta
- Comportamento do `GlobalExceptionHandler` (como erros são serializados)
- Mapeamento de URLs (se o endpoint está no path certo)

---

**O que faria para adicionar testes de integração?**

Usaria **Testcontainers** para subir PostgreSQL e MinIO em containers Docker durante os testes. Com `@SpringBootTest` + Testcontainers, o contexto Spring completo sobe apontando para os containers reais, e você testa o fluxo completo do request até o banco, sem mocks. Isso pega problemas que testes unitários não pegam: queries JPA incorretas, migrations com erro, transações mal configuradas.

---

## Docker

---

**O que é multi-stage build? Qual o benefício prático?**

O Dockerfile tem dois estágios:
1. **Build stage** (`FROM maven:3.9.9`): tem JDK completo + Maven. Compila e empacota o JAR. Essa imagem tem ~500MB.
2. **Runtime stage** (`FROM eclipse-temurin:17-jre`): tem apenas o JRE (sem Maven, sem código-fonte). Copia apenas o JAR gerado.

A imagem final tem ~200MB ao invés de ~500MB. Em produção, a imagem menor significa pull mais rápido, menos superfície de ataque e menos custo de registry.

---

**Por que o app depende do healthcheck do Postgres ao invés de só `depends_on`?**

`depends_on` garante apenas que o **container** do Postgres iniciou, não que o **serviço** PostgreSQL dentro dele está pronto para aceitar conexões. O banco leva alguns segundos para inicializar após o container subir. Sem o healthcheck, a aplicação Spring Boot tentaria conectar antes do banco estar pronto e falharia na inicialização. Com `condition: service_healthy`, o Docker Compose espera o `pg_isready` retornar sucesso antes de iniciar o container da aplicação.

---

**Como as variáveis de ambiente do docker-compose chegam ao `application.properties`?**

O `application.properties` usa a sintaxe `${NOME_VARIAVEL:valor_default}`. Quando a aplicação sobe no container, o Spring lê as variáveis de ambiente do sistema operacional e as injeta nos placeholders. O docker-compose define essas variáveis no bloco `environment` do serviço. Exemplo:

```yaml
# docker-compose.yml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/ocorrencias
```

```properties
# application.properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/ocorrencias}
```

O valor após `:` é o default usado quando a variável não está definida (desenvolvimento local sem Docker).

---

## Design e Decisões

---

**Por que a finalização de ocorrência é irreversível?**

É uma decisão de negócio para garantir integridade dos dados. Uma ocorrência finalizada representa um estado encerrado e auditável — como um processo concluído. Permitir reversão abriria margem para adulteração de registros históricos, o que é especialmente crítico se as ocorrências têm valor legal ou de auditoria. Se fosse necessário reverter, o design correto seria criar uma nova ocorrência referenciando a anterior, mantendo o histórico completo.

---

**O que é a whitelist de campos de ordenação? Por que é uma questão de segurança?**

Se você aceita o campo de ordenação diretamente do usuário e passa para a query sem validação, um atacante pode enviar `sort=senha` ou `sort=codUsuario` e inferir dados pelo comportamento da ordenação (side-channel). Além disso, pode enviar SQL injection via parâmetro de sort em implementações menos robustas.

No projeto, apenas `dtaOcorrencia` e `nmeCidade` são campos aceitos. Qualquer outro valor é rejeitado ou ignorado, e o default `dtaOcorrencia DESC` é aplicado.

---

**Por que `ModelMapper` em modo STRICT? Qual o risco do modo padrão?**

No modo padrão (`STANDARD`), o ModelMapper tenta ser "inteligente" e mapeia campos com nomes similares ou hierarquias aninhadas automaticamente. Isso pode mapear campos errados silenciosamente — por exemplo, `codCliente` de uma entidade sendo mapeado para `codEndereco` de um DTO se os nomes forem parecidos o suficiente para o algoritmo de matching. No modo `STRICT`, cada campo só é mapeado se os nomes forem exatamente iguais, tornando o comportamento previsível e erros de mapeamento fáceis de detectar.

---

**Por que criar `CriteriaQueryUtils` genérico?**

Sem o utilitário, cada `RepositoryImpl` repetiria o mesmo código de criação de `CriteriaQuery`, `CriteriaBuilder`, aplicação de predicates, paginação e sort. Com três repositórios, isso seria triplicado. O `CriteriaQueryUtils` extrai essa lógica comum usando generics (`<T>` para a entidade), e os repositórios só precisam fornecer os predicates específicos do seu domínio via `CriteriaFilter`. É o padrão Template Method aplicado a queries dinâmicas.

---

**Se o projeto crescesse, o que você refatoraria primeiro?**

Três pontos prioritários:

1. **Consistência distribuída com MinIO**: implementar um mecanismo de compensação ou outbox pattern para garantir que arquivos órfãos no MinIO sejam removidos quando a transação do banco falha

2. **Testes de integração com Testcontainers**: os testes atuais mockam repositórios e storage, então não detectam problemas reais de queries JPA ou migrations. Testcontainers resolveria isso

3. **Paginação no response**: o endpoint de listagem retorna `Page<T>` do Spring, mas seria melhor envolver em um DTO com metadados explícitos (`totalElements`, `totalPages`, `currentPage`) para que os clientes não dependam da estrutura interna do Spring