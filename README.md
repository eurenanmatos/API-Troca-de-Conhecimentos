# API Troca de Conhecimentos

API RESTful desenvolvida em Java com padrão MVC (Controller, Service, DAO/Repository), autenticação JWT e banco de dados MySQL.

---

## 🛠️ Tecnologias

- Java 11
- Maven
- Tomcat 7 (via plugin Maven)
- MySQL 8
- JJWT 0.11.5 (JSON Web Token)

---

## ⚙️ Configuração

### Banco de dados

Configure as variáveis de ambiente (opcional — padrões já definidos):

| Variável     | Padrão                                              |
|--------------|-----------------------------------------------------|
| `DB_URL`     | `jdbc:mysql://localhost:3306/banco2?useSSL=false`   |
| `DB_USER`    | `root`                                              |
| `DB_PASS`    | *(vazio)*                                           |
| `JWT_SECRET` | `TrocaConhecimentosSecretKey2025!!`                 |

As tabelas são criadas automaticamente na primeira execução.

### Executar

```bash
mvn tomcat7:run
```

API disponível em: `http://localhost:8080/api-troca`

---

## 🔐 Autenticação JWT

Todas as rotas (exceto `/login`) exigem um token JWT no cabeçalho:

```
Authorization: Bearer SEU_TOKEN
```

Se o token não for enviado ou for inválido, a API retorna:

```json
{ "erro": "Acesso não autorizado: token inválido ou expirado." }
```

---

## 📌 Endpoints

### 🔓 Login (público)

#### `POST /login`

Autentica o usuário e retorna um token JWT.

**Body:**
```json
{
  "email": "renan@example.com",
  "senha": "1234"
}
```

**Resposta 200:**
```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

**Resposta 401:**
```json
{ "erro": "Acesso não autorizado: e-mail ou senha inválidos." }
```

---

### 👤 Usuários (protegido)

> Todas as rotas abaixo exigem `Authorization: Bearer TOKEN`

#### `GET /usuarios`
Lista todos os usuários.

#### `GET /usuarios/{id}`
Busca usuário por ID.

#### `POST /usuarios`
Cria um novo usuário.

**Body:**
```json
{
  "nome": "Carlos",
  "email": "carlos@example.com",
  "senha": "1234",
  "habilidadeOferecidaId": 1,
  "habilidadeDesejadaId": 2
}
```

**Resposta 201:**
```json
{
  "id": 3,
  "nome": "Carlos",
  "email": "carlos@example.com",
  "habilidadeOferecida": { "id": 1, "nome": "Java", "descricao": "Programação em Java" },
  "habilidadeDesejada": { "id": 2, "nome": "SQL", "descricao": "Consultas em banco de dados" }
}
```

#### `PUT /usuarios/{id}`
Atualiza um usuário existente.

**Body:**
```json
{
  "nome": "Carlos Atualizado",
  "email": "carlos.novo@example.com",
  "habilidadeOferecidaId": 2,
  "habilidadeDesejadaId": 1
}
```

#### `DELETE /usuarios/{id}`
Remove um usuário. Retorna `204 No Content`.

---

### 🧠 Habilidades (protegido)

#### `GET /habilidades`
Lista todas as habilidades.

#### `GET /habilidades/{id}`
Busca habilidade por ID.

#### `POST /habilidades`
Cria nova habilidade.

**Body:**
```json
{
  "nome": "Python",
  "descricao": "Linguagem de programação Python"
}
```

**Resposta 201:**
```json
{ "id": 3, "nome": "Python", "descricao": "Linguagem de programação Python" }
```

#### `PUT /habilidades/{id}`
Atualiza uma habilidade.

**Body:**
```json
{
  "nome": "Python 3",
  "descricao": "Python versão 3.x"
}
```

#### `DELETE /habilidades/{id}`
Remove uma habilidade. Retorna `204 No Content`.

---

### 🔄 Trocas (protegido)

#### `GET /trocas`
Lista todas as trocas.

#### `GET /trocas/{id}`
Busca troca por ID.

#### `POST /trocas`
Solicita uma nova troca.

**Body:**
```json
{
  "solicitanteId": 1,
  "destinatarioId": 2,
  "habilidadeOferecidaId": 1,
  "habilidadeDesejadaId": 2
}
```

**Resposta 201:**
```json
{
  "id": 1,
  "solicitante": { "id": 1, "nome": "Renan", "email": "renan@example.com" },
  "destinatario": { "id": 2, "nome": "Ana",   "email": "ana@example.com" },
  "habilidadeOferecida": { "id": 1, "nome": "Java", "descricao": "..." },
  "habilidadeDesejada":  { "id": 2, "nome": "SQL",  "descricao": "..." },
  "status": "PENDENTE"
}
```

#### `PUT /trocas/{id}`
Atualiza o status de uma troca.

**Body:**
```json
{ "status": "ACEITA" }
```

Status possíveis: `PENDENTE`, `ACEITA`, `RECUSADA`, `CANCELADA`

#### `DELETE /trocas/{id}`
Remove uma troca. Retorna `204 No Content`.

---

## 🗄️ Estrutura do Banco de Dados

```sql
CREATE TABLE habilidades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255) DEFAULT ''
);

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    habilidade_oferecida_id INT,
    habilidade_desejada_id INT,
    FOREIGN KEY (habilidade_oferecida_id) REFERENCES habilidades(id),
    FOREIGN KEY (habilidade_desejada_id) REFERENCES habilidades(id)
);

CREATE TABLE trocas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_oferecendo_id INT,
    usuario_interessado_id INT,
    habilidade_oferecida_id INT,
    habilidade_desejada_id INT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
    FOREIGN KEY (usuario_oferecendo_id) REFERENCES usuarios(id),
    FOREIGN KEY (usuario_interessado_id) REFERENCES usuarios(id),
    FOREIGN KEY (habilidade_oferecida_id) REFERENCES habilidades(id),
    FOREIGN KEY (habilidade_desejada_id) REFERENCES habilidades(id)
);
```

---

## 📂 Estrutura do Projeto

```
src/
├── controller/
│   ├── HabilidadeController.java
│   ├── TrocaController.java
│   └── UsuarioController.java
├── service/
│   ├── HabilidadeService.java
│   ├── TrocaService.java
│   └── UsuarioService.java        ← inclui lógica de login
├── repository/
│   ├── HabilidadeRepository.java
│   ├── TrocaRepository.java
│   └── UsuarioRepository.java
├── model/
│   ├── Habilidade.java
│   ├── Troca.java
│   └── Usuario.java
├── servlet/                       ← Camada HTTP (substitui o DAO de web)
│   ├── HabilidadeServlet.java
│   ├── LoginServlet.java
│   ├── TrocaServlet.java
│   └── UsuarioServlet.java
├── security/
│   ├── AuthFilter.java            ← Filtro JWT para rotas protegidas
│   └── JwtUtil.java               ← Geração e validação de token JWT
├── util/
│   ├── Database.java
│   └── JsonUtil.java
└── listener/
    └── StartupListener.java       ← Inicializa banco e dados de exemplo
```

---

## 🧪 Exemplo de uso com Postman / curl

```bash
# 1. Login
curl -X POST http://localhost:8080/api-troca/login \
  -H "Content-Type: application/json" \
  -d '{"email":"renan@example.com","senha":"1234"}'

# 2. Usar o token retornado nas demais rotas
curl http://localhost:8080/api-troca/usuarios \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```
