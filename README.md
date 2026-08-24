# API de Gestão de Pedidos

API REST desenvolvida em Java e Spring Boot para gerenciamento de clientes, produtos, pedidos e itens de pedido. O projeto possui autenticação JWT, controle de permissões por role, regras de estoque e ciclo de vida do pedido, migrations com Flyway, documentação Swagger, testes automatizados e ambiente Docker.

## Funcionalidades

- Cadastro e autenticação de usuários com BCrypt e JWT.
- Perfis `USER` e `ADMIN`.
- CRUD de clientes.
- CRUD de produtos com controle de estoque.
- Criação e gerenciamento de pedidos.
- Inclusão, alteração e remoção de itens de pedido.
- Cálculo automático de subtotal e total do pedido.
- Baixa automática de estoque ao adicionar itens.
- Devolução de estoque ao remover itens ou cancelar pedidos.
- Controle de transições de status do pedido.
- Respostas de erro padronizadas em JSON.
- Documentação OpenAPI/Swagger.
- Testes unitários, de integração e fluxo ponta a ponta.

## Tecnologias

- Java 17
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA / Hibernate
- Spring Security
- JWT (`java-jwt`)
- BCrypt
- PostgreSQL 16
- Flyway
- Maven / Maven Wrapper
- Docker / Docker Compose
- Swagger / OpenAPI
- JUnit
- Mockito
- MockMvc
- H2 para testes

## Arquitetura

O projeto segue uma separação em camadas:

```text
HTTP Request
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
JPA / Hibernate
    ↓
PostgreSQL
```

DTOs de request e response evitam expor diretamente as entidades da aplicação. Mappers são usados para conversão entre objetos quando necessário.

A autenticação segue o fluxo:

```text
POST /auth/login
    ↓
Validação de email e senha com BCrypt
    ↓
Geração do JWT
    ↓
Authorization: Bearer <token>
    ↓
JwtAuthenticationFilter
    ↓
Spring Security
    ↓
Endpoint protegido
```

## Regras de negócio

### Pedidos

Um novo pedido deve iniciar em `CREATED`.

Fluxo permitido:

```text
CREATED → CONFIRMED → SHIPPED → DELIVERED
   ↓          ↓
CANCELLED   CANCELLED
```

Pedidos `DELIVERED` ou `CANCELLED` não podem ser modificados. Itens só podem ser alterados enquanto o pedido estiver em `CREATED`.

Ao cancelar um pedido, o estoque dos itens é devolvido automaticamente.

### Estoque

Ao adicionar um item:

```text
subtotal = unitPrice × quantity
```

A quantidade é descontada do estoque e o subtotal é somado ao total do pedido. PUT, PATCH e DELETE recalculam estoque e valores de forma transacional.

Produtos inativos não podem ser adicionados a pedidos e pedidos com quantidade maior que o estoque disponível retornam conflito.

## Segurança

O cadastro público cria usuários com role `USER`.

Permissões atuais para produtos:

| Operação | USER | ADMIN |
|---|:---:|:---:|
| GET `/products` | ✅ | ✅ |
| GET `/products/{id}` | ✅ | ✅ |
| POST `/products` | ❌ | ✅ |
| PUT `/products/{id}` | ❌ | ✅ |
| PATCH `/products/{id}` | ❌ | ✅ |
| DELETE `/products/{id}` | ❌ | ✅ |

Os demais recursos exigem usuário autenticado. Endpoints de autenticação e Swagger são públicos.

Em desenvolvimento, uma conta pode ser promovida para `ADMIN` diretamente no banco:

```sql
UPDATE users
SET role = 'ADMIN'
WHERE email = 'admin@email.com';
```

Depois disso, faça login novamente para gerar um JWT contendo a nova role.

## Principais endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/auth/register` | Cadastrar usuário |
| POST | `/auth/login` | Fazer login e gerar JWT |
| GET | `/customers` | Listar clientes |
| POST | `/customers` | Criar cliente |
| GET | `/customers/{id}` | Buscar cliente |
| PUT | `/customers/{id}` | Atualizar cliente |
| PATCH | `/customers/{id}` | Atualizar parcialmente cliente |
| DELETE | `/customers/{id}` | Excluir cliente |
| GET | `/products` | Listar produtos |
| POST | `/products` | Criar produto (`ADMIN`) |
| GET | `/products/{id}` | Buscar produto |
| PUT | `/products/{id}` | Atualizar produto (`ADMIN`) |
| PATCH | `/products/{id}` | Atualizar parcialmente produto (`ADMIN`) |
| DELETE | `/products/{id}` | Excluir produto (`ADMIN`) |
| GET | `/orders` | Listar pedidos |
| POST | `/orders` | Criar pedido |
| GET | `/orders/{id}` | Buscar pedido |
| PUT | `/orders/{id}` | Atualizar pedido |
| PATCH | `/orders/{id}` | Alterar parcialmente/status |
| DELETE | `/orders/{id}` | Excluir pedido sem itens |
| GET | `/order-items` | Listar itens |
| POST | `/order-items` | Adicionar item ao pedido |
| GET | `/order-items/{id}` | Buscar item |
| PUT | `/order-items/{id}` | Atualizar item |
| PATCH | `/order-items/{id}` | Atualizar parcialmente item |
| DELETE | `/order-items/{id}` | Remover item |

## Swagger

Com a aplicação em execução:

```text
http://localhost:8080/swagger-ui/index.html
```

Para testar endpoints protegidos:

1. Cadastre um usuário em `/auth/register`.
2. Faça login em `/auth/login`.
3. Copie o campo `token` da resposta.
4. Clique em **Authorize** no Swagger.
5. Informe o JWT e execute os endpoints protegidos.

## Executando com Docker Compose

### Requisitos

- Docker Desktop / Docker Engine
- Docker Compose

Crie seu arquivo `.env` a partir do exemplo:

```bash
cp .env.example .env
```

No Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Altere principalmente `POSTGRES_PASSWORD` e `JWT_SECRET` antes de usar fora de ambiente local.

Suba PostgreSQL e API:

```bash
docker compose up --build
```

O Compose cria o PostgreSQL, aguarda o healthcheck do banco e só então inicia a API. O Flyway aplica as migrations automaticamente.

Para encerrar:

```bash
docker compose down
```

Para remover também o volume do banco:

```bash
docker compose down -v
```

## Executando localmente sem Docker para a API

Defina as variáveis de ambiente:

```text
DB_URL=jdbc:postgresql://localhost:5432/gestao_pedidos
DB_USERNAME=postgres
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_jwt
```

Depois execute pela IDE ou pelo Maven Wrapper.

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

## Testes

Os testes utilizam H2 em memória com o profile `test`, portanto não dependem do PostgreSQL local.

Windows:

```powershell
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

A suíte inclui testes de services, autenticação, JWT, segurança e um `EndToEndFlowTest` que cobre o fluxo principal:

```text
register
→ login
→ 401 sem token
→ 403 USER em operação ADMIN
→ criação de produto por ADMIN
→ criação de cliente
→ criação de pedido
→ adição de item
→ baixa de estoque
→ cálculo do total
→ cancelamento
→ devolução do estoque
→ 404
→ 409
```

## Respostas de erro

A API utiliza uma estrutura padronizada:

```json
{
  "timestamp": "2026-08-24T17:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "path": "/products/999"
}
```

São tratados, entre outros, `400`, `401`, `403`, `404` e `409`.

## Estrutura principal

```text
src/main/java/com/nisholas/ordermanagement
├── config
├── controller
├── entity
├── exception
├── Mapper
├── repository
├── request
├── response
├── security
└── service
```

## Autor

Matheus Nísholas
