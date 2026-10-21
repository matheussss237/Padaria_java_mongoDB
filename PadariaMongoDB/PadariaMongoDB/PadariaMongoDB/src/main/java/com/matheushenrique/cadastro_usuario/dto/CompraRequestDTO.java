package com.matheushenrique.cadastro_usuario.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CompraRequestDTO {
    // Um id por unidade: [1, 1, 2] = 2x produto 1 + 1x produto 2
    @NotEmpty(message = "A compra precisa ter pelo menos um produto")
    private List<String> produtosIds;
    private String endereco;
    private String cidade;
    private String uf;
    private String formaPagamento;
}
