package com.matheushenrique.cadastro_usuario.config;

import com.matheushenrique.cadastro_usuario.business.AuthorizationService;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Padaria;
import com.matheushenrique.cadastro_usuario.infrastructure.repository.PadariaRepository;
import com.matheushenrique.cadastro_usuario.role.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// Popula o banco com os produtos da vitrine e com o usuário administrador
@Component
public class DataInitializer implements CommandLineRunner {

    private final PadariaRepository padariaRepository;
    private final AuthorizationService authorizationService;

    @Value("${app.admin.login}")
    private String adminLogin;

    @Value("${app.admin.senha}")
    private String adminSenha;

    public DataInitializer(PadariaRepository padariaRepository, AuthorizationService authorizationService) {
        this.padariaRepository = padariaRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public void run(String... args) {
        if (!authorizationService.userExists(adminLogin)) {
            authorizationService.registerUser(adminLogin, adminSenha, UserRole.ADMIN);
        }

        if (padariaRepository.count() == 0) {
            padariaRepository.saveAll(List.of(
                    produto("Pão francês", 6.00, "Pão francês crocante, saído do forno.", "paes", "img/pao.jpg"),
                    produto("Brigadeiro", 16.00, "Tradicional docinho brasileiro — embalagem com 6 unidades.", "doces", "img/brigadeiro.jpg"),
                    produto("Coxinha", 5.50, "Coxinha de frango cremosa.", "salgados", "img/Caxinha.jpg"),
                    produto("Café Espresso", 3.50, "Dose de café espresso, feito na hora.", "bebidas", "img/Cafe3.jpg"),
                    produto("Misto quente completo", 14.99, "Misto quente completo.", "salgados", "img/misto.jpg"),
                    produto("Sonho", 27.89, "Sonho recheado com creme.", "doces", "img/Sonho.jpg"),
                    produto("Bolo de caramelo", 50.99, "Bolo de caramelo inteiro.", "doces", "img/Bolo de caramelo.jpg"),
                    produto("Bolo de cenoura", 35.99, "Bolo de cenoura com cobertura de chocolate.", "doces", "img/Bolo de cenoura.jpg")
            ));
        }
    }

    private Padaria produto(String nome, double preco, String descricao, String categoria, String imagem) {
        return Padaria.builder()
                .nome(nome)
                .preco(preco)
                .descricao(descricao)
                .categoria(categoria)
                .imagem(imagem)
                .build();
    }
}
