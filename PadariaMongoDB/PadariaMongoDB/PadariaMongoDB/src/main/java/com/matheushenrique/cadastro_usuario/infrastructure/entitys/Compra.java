package com.matheushenrique.cadastro_usuario.infrastructure.entitys;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "compras")

public class Compra {
    @Id
    private String id;

    // Cliente e produtos ficam embutidos no documento: a compra guarda os
    // dados e preços do momento em que foi feita
    private Usuario usuario;

    // Produto repetido na lista = mais de uma unidade
    private List<Padaria> produtos;

    private LocalDateTime dataCompra;

    private Double valorTotal;

    private String endereco;

    private String cidade;

    private String uf;

    private String formaPagamento;

}
