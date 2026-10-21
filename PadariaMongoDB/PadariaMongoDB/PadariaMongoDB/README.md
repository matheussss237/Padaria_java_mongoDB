# 🍞 Padaria Doce & Sabor - Sistema de Gestão

Sistema web para padaria desenvolvido em **Java com Spring Boot**, com banco **MongoDB**.
Permite cadastro e login de clientes, vitrine de produtos, carrinho, finalização de compra e
gerenciamento da própria conta. O administrador pode cadastrar, alterar e remover produtos.

---

## 🚀 Tecnologias utilizadas
- Java 17+
- Spring Boot 3 (Web, Data MongoDB, Security, Validation)
- MongoDB
- Lombok
- Maven (wrapper incluso: `./mvnw`)
- HTML, CSS e JavaScript puro no front-end
- Docker (opcional)

---

## ⚙️ Como executar localmente

### Pré-requisitos
- Java 17 ou superior
- MongoDB rodando em `localhost:27017` (ou informe outro endereço em `MONGODB_URI`, ex: MongoDB Atlas)

> Os testes (`./mvnw test`) usam um MongoDB em memória e não precisam de Mongo instalado.

### Passos
```bash
# Acessar a pasta do projeto
cd PadariaMongoDB/PadariaMongoDB/PadariaMongoDB

# Rodar os testes
./mvnw test

# Executar a aplicação
./mvnw spring-boot:run
```

Depois acesse **http://localhost:8080** (tela de cadastro).

| Página | Descrição |
|---|---|
| `/index.html` | Cadastro de cliente |
| `/usu.html` | Login (CPF + senha) |
| `/produtos.html` | Vitrine com busca e filtro por categoria |
| `/carrinho.html` | Carrinho de compras |
| `/compra.html` | Finalizar compra (exige login) |
| `/atualizar.html` | Minha conta: atualizar dados, trocar senha, excluir conta e ver compras |

Os ids são `ObjectId` do Mongo (texto). Ao iniciar, se a coleção estiver vazia, a aplicação cria automaticamente os 8 produtos da vitrine e o usuário
administrador **`admin` / `admin123`**.

### Variáveis de ambiente (opcionais)
| Variável | Padrão | Uso |
|---|---|---|
| `ADMIN_LOGIN` / `ADMIN_SENHA` | `admin` / `admin123` | Usuário administrador criado ao iniciar. **Troque em produção.** |
| `MONGODB_URI` | `mongodb://localhost:27017/usuarios` | Endereço do MongoDB. |
| `CORS_ORIGENS` | `http://localhost:63342,...` | Origens liberadas quando o front é aberto fora do Spring (IntelliJ, Live Server). |
| `PORT` | `8080` | Porta HTTP. |

### Docker
```bash
docker build -t padaria .
docker run -p 8080:8080 -e MONGODB_URI=mongodb://host.docker.internal:27017/usuarios \
  -e ADMIN_SENHA=umaSenhaForte padaria
```

---

## 🔐 Autenticação

A API usa **HTTP Basic** (sem sessão). O login do cliente é o **CPF** (somente números).
O front guarda as credenciais no `sessionStorage` depois do login.

- Visitantes podem ver os produtos.
- Clientes logados podem comprar e ver/alterar/excluir **apenas o próprio** cadastro.
- O administrador pode alterar produtos e listar todos os usuários e compras.

---

## 📡 Endpoints da API

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/auth/register` | público | Cadastra cliente `{nome, cpf, email, dataNascimento, telefone, senha}` |
| POST | `/auth/login` | público | Confere `{login, senha}` e devolve `{login, role, nome}` |
| PUT | `/auth/senha` | logado | Altera a senha `{senha}` |
| GET | `/padaria` | público | Lista produtos |
| GET | `/padaria/{id}` | público | Busca produto |
| POST | `/padaria` | admin | Cadastra produto `{nome, preco, descricao, categoria, imagem}` |
| PUT | `/padaria/{id}` | admin | Atualiza produto (só os campos enviados) |
| DELETE | `/padaria/{id}` | admin | Remove produto (as compras guardam uma cópia do produto) |
| GET | `/usuarios` | admin | Lista clientes |
| POST | `/usuarios` | admin | Cadastra cliente sem login |
| GET | `/usuarios/{cpf}` | dono ou admin | Busca cliente |
| PUT | `/usuarios/{cpf}` | dono ou admin | Atualiza cliente (só os campos enviados) |
| DELETE | `/usuarios/{cpf}` | dono ou admin | Exclui cliente, suas compras e seu login |
| POST | `/compras` | logado | Cria compra `{produtosIds, endereco, cidade, uf, formaPagamento}` — repita o id para mais unidades |
| GET | `/compras/minhas` | logado | Compras do cliente logado |
| GET | `/compras` | admin | Todas as compras |

Exemplo:
```bash
curl -u admin:admin123 -X POST localhost:8080/padaria \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Pão de queijo","preco":4.5,"categoria":"salgados","imagem":"img/pao.jpg"}'
```

---

✍️ Autor

Pablo Phelipe Inácio Lopes

👨‍🏫 Orientador

Professor João Keslei Carvalho Viana

📜 Licença

Este projeto é de uso educacional, aberto a colaborações.
