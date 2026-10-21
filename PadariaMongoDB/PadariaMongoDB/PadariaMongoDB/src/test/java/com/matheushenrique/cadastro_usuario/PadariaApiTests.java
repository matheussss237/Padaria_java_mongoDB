package com.matheushenrique.cadastro_usuario;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PadariaApiTests {

    @DynamicPropertySource
    static void mongo(DynamicPropertyRegistry registry) {
        MongoEmMemoria.registrar(registry);
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private static final String CPF = "12345678901";
    private static final String SENHA = "senha123";

    private void cadastrarCliente(String cpf) throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Maria","cpf":"%s","email":"maria@email.com",
                                 "dataNascimento":"2000-01-01","telefone":"11999999999","senha":"%s"}
                                """.formatted(cpf, SENHA)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value(cpf));
    }

    private JsonNode listarProdutos() throws Exception {
        String json = mvc.perform(get("/padaria")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(json);
    }

    @Test
    void paginasEstaticasSaoPublicas() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/produtos.html")).andExpect(status().isOk());
        mvc.perform(get("/css/pod.css")).andExpect(status().isOk());
        mvc.perform(get("/js/api.js")).andExpect(status().isOk());
        mvc.perform(get("/nao-existe.html")).andExpect(status().isNotFound());
    }

    @Test
    void catalogoVemPopuladoEPublico() throws Exception {
        JsonNode produtos = listarProdutos();
        org.junit.jupiter.api.Assertions.assertEquals(8, produtos.size());
        String id = produtos.get(0).get("id").asText();
        mvc.perform(get("/padaria/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").isNotEmpty());
        mvc.perform(get("/padaria/naoexiste")).andExpect(status().isNotFound());
    }

    @Test
    void cadastroLoginECompra() throws Exception {
        cadastrarCliente(CPF);

        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"%s\",\"senha\":\"%s\"}".formatted(CPF, SENHA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.role").value("user"));

        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"%s\",\"senha\":\"errada\"}".formatted(CPF)))
                .andExpect(status().isUnauthorized());

        JsonNode produtos = listarProdutos();
        String p1 = produtos.get(0).get("id").asText();
        double preco1 = produtos.get(0).get("preco").asDouble();
        String p2 = produtos.get(1).get("id").asText();
        double preco2 = produtos.get(1).get("preco").asDouble();

        // sem login não compra
        mvc.perform(post("/compras").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"produtosIds\":[\"%s\"]}".formatted(p1)))
                .andExpect(status().isUnauthorized());

        // produto repetido conta como 2 unidades
        mvc.perform(post("/compras").with(httpBasic(CPF, SENHA)).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"produtosIds":["%s","%s","%s"],"endereco":"Rua A, 1","cidade":"São Paulo",
                                 "uf":"SP","formaPagamento":"pix"}
                                """.formatted(p1, p1, p2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.produtos", hasSize(3)))
                .andExpect(jsonPath("$.valorTotal").value(closeTo(preco1 * 2 + preco2, 0.001)))
                .andExpect(jsonPath("$.usuario.cpf").value(CPF));

        mvc.perform(post("/compras").with(httpBasic(CPF, SENHA)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"produtosIds\":[]}"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/compras").with(httpBasic(CPF, SENHA)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"produtosIds\":[\"naoexiste\"]}"))
                .andExpect(status().isNotFound());

        mvc.perform(get("/compras/minhas").with(httpBasic(CPF, SENHA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void cadastroDuplicadoEInvalido() throws Exception {
        cadastrarCliente(CPF);
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Outro\",\"cpf\":\"%s\",\"senha\":\"%s\"}".formatted(CPF, SENHA)))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"cpf\":\"123\",\"senha\":\"1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.cpf").exists());
    }

    @Test
    void clienteSoAcessaOProprioCadastro() throws Exception {
        cadastrarCliente(CPF);
        cadastrarCliente("99988877766");

        mvc.perform(get("/usuarios/" + CPF).with(httpBasic(CPF, SENHA)))
                .andExpect(status().isOk());
        mvc.perform(get("/usuarios/99988877766").with(httpBasic(CPF, SENHA)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/usuarios").with(httpBasic(CPF, SENHA)))
                .andExpect(status().isForbidden());

        mvc.perform(put("/usuarios/" + CPF).with(httpBasic(CPF, SENHA)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Maria Silva\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria@email.com"));

        mvc.perform(put("/auth/senha").with(httpBasic(CPF, SENHA)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senha\":\"novaSenha1\"}"))
                .andExpect(status().isNoContent());
        mvc.perform(get("/usuarios/" + CPF).with(httpBasic(CPF, "novaSenha1")))
                .andExpect(status().isOk());

        String produto = listarProdutos().get(0).get("id").asText();
        mvc.perform(post("/compras").with(httpBasic(CPF, "novaSenha1")).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"produtosIds\":[\"%s\"]}".formatted(produto)))
                .andExpect(status().isCreated());

        // excluir conta remove cadastro, compras e login
        mvc.perform(delete("/usuarios/" + CPF).with(httpBasic(CPF, "novaSenha1")))
                .andExpect(status().isNoContent());
        mvc.perform(get("/usuarios/" + CPF).with(httpBasic(CPF, "novaSenha1")))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/usuarios").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void somenteAdminAlteraProdutos() throws Exception {
        cadastrarCliente(CPF);
        String novo = "{\"nome\":\"Pão de queijo\",\"preco\":4.5,\"categoria\":\"salgados\"}";

        mvc.perform(post("/padaria").contentType(MediaType.APPLICATION_JSON).content(novo))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/padaria").with(httpBasic(CPF, SENHA)).contentType(MediaType.APPLICATION_JSON).content(novo))
                .andExpect(status().isForbidden());

        String json = mvc.perform(post("/padaria").with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON).content(novo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = mapper.readTree(json).get("id").asText();

        mvc.perform(put("/padaria/" + id).with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"preco\":5.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preco").value(5.0))
                .andExpect(jsonPath("$.nome").value("Pão de queijo"));

        mvc.perform(delete("/padaria/" + id).with(httpBasic("admin", "admin123")))
                .andExpect(status().isNoContent());
        mvc.perform(get("/padaria/" + id)).andExpect(status().isNotFound());

        // a compra guarda uma cópia do produto, então ele pode ser apagado depois
        String vendido = listarProdutos().get(0).get("id").asText();
        mvc.perform(post("/compras").with(httpBasic(CPF, SENHA)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"produtosIds\":[\"%s\"]}".formatted(vendido)))
                .andExpect(status().isCreated());
        mvc.perform(delete("/padaria/" + vendido).with(httpBasic("admin", "admin123")))
                .andExpect(status().isNoContent());

        mvc.perform(get("/compras").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
