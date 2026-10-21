package com.matheushenrique.cadastro_usuario.infrastructure.entitys;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document(collection = "padaria")

public class Padaria {

    @Id
    private String id;

    private String nome;

    private Double preco;

    private String descricao;

    // paes, doces, salgados ou bebidas (usado no filtro da página de produtos)
    private String categoria;

    // caminho da imagem dentro de /static (ex: img/Sonho.jpg)
    private String imagem;

}
