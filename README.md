# 🍞 Padaria API — Spring Boot + MongoDB

API REST para gestão de uma padaria: cadastro de **produtos**, **clientes** e **compras**, com os dados guardados no **MongoDB** usando **Spring Data MongoDB**. O projeto também traz um front-end simples em HTML, CSS e JavaScript servido pelo próprio Spring Boot.

> Projeto de estudo desenvolvido durante o curso Técnico em Desenvolvimento de Sistemas (Senac-DF).
> É a evolução da versão relacional com H2: [Padaria_java_H2](https://github.com/matheussss237/Padaria_java_H2).

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.3 (Web, Data MongoDB) |
| Banco de dados | MongoDB (local ou MongoDB Atlas) |
| Produtividade | Lombok |
| Build | Maven |
| Front-end | HTML, CSS e JavaScript (pasta `static`) |
| Deploy | Docker |

## Arquitetura

```
controller  →  business (services)  →  infrastructure/repository (MongoRepository)  →  MongoDB
                      ↓
              infrastructure/entitys (@Document)  ·  dto  ·  exceptions
```

Coleções: `padaria` (produtos), `usuarios` (clientes) e `compras`.

## Endpoints

| Recurso | Método | Rota | Descrição |
|---|---|---|---|
| Produtos | `POST` | `/padaria` | Cadastra um produto |
| Produtos | `GET` / `PUT` / `DELETE` | `/padaria/...` | Busca, atualiza e remove produtos |
| Clientes | `GET` | `/usuarios` | Lista todos os clientes |
| Clientes | `POST` | `/usuarios` | Cadastra um cliente |
| Clientes | `GET` | `/usuarios/{cpf}` | Busca um cliente pelo CPF |
| Clientes | `PUT` | `/usuarios/{cpf}` | Atualiza um cliente |
| Clientes | `DELETE` | `/usuarios/{cpf}` | Remove um cliente |
| Compras | `POST` | `/compras` | Registra uma compra e calcula o valor total |

## Como executar

**Pré-requisitos:** Java 17 ou superior e um MongoDB rodando (local ou Atlas).

A conexão fica em `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/usuarios
```

Para usar o MongoDB Atlas, troque pela sua connection string — de preferência por variável de ambiente, sem subir a senha para o GitHub:

```bash
SPRING_DATA_MONGODB_URI="mongodb+srv://usuario:senha@cluster.mongodb.net/padaria" ./mvnw spring-boot:run
```

Rodando localmente:

```bash
git clone https://github.com/matheussss237/Padaria_java_mongoDB.git
cd Padaria_java_mongoDB
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd spring-boot:run`, ou abra a pasta no IntelliJ IDEA e rode a classe `CadastroUsuarioApplication`. Depois, acesse `http://localhost:8080`.

## Próximos passos

- [ ] Autenticação com Spring Security e JWT
- [ ] Testes automatizados com banco em memória
- [ ] Documentação da API com Swagger / OpenAPI

---

Desenvolvido por **Matheus Henrique** · [Portfólio](https://matheussss237.github.io/portfolio/) · [LinkedIn](https://linkedin.com/in/matheushenriquem)
