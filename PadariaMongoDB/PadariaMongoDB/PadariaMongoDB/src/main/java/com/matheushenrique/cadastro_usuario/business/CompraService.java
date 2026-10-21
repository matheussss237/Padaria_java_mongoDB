package com.matheushenrique.cadastro_usuario.business;

import com.matheushenrique.cadastro_usuario.dto.CompraRequestDTO;
import com.matheushenrique.cadastro_usuario.exceptions.RegraNegocioException;
import com.matheushenrique.cadastro_usuario.exceptions.ResourceNotFoundException;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Compra;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Padaria;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Usuario;
import com.matheushenrique.cadastro_usuario.infrastructure.repository.CompraRepository;
import com.matheushenrique.cadastro_usuario.infrastructure.repository.PadariaRepository;
import com.matheushenrique.cadastro_usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class CompraService {

    private final CompraRepository compraRepository;
    private final UsuarioRepository usuarioRepository;
    private final PadariaRepository padariaRepository;

    private Double calcularValorTotal(List<Padaria> produtos) {
        BigDecimal total = produtos.stream()
                .map(produto -> BigDecimal.valueOf(produto.getPreco()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // A compra é sempre feita em nome do cliente logado (login = CPF)
    public Compra criarCompraComDTO(String cpf, CompraRequestDTO dto){
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RegraNegocioException("Esse login não possui cadastro de cliente"));

        // findAllById ignora ids repetidos, então montamos a lista respeitando a quantidade
        Map<String, Padaria> encontrados = padariaRepository.findAllById(dto.getProdutosIds()).stream()
                .collect(Collectors.toMap(Padaria::getId, Function.identity()));
        List<Padaria> produtos = dto.getProdutosIds().stream()
                .map(id -> {
                    Padaria produto = encontrados.get(id);
                    if (produto == null) {
                        throw new ResourceNotFoundException("Produto não encontrado: " + id);
                    }
                    return produto;
                })
                .collect(Collectors.toList());

        Compra compra = Compra.builder()
                .usuario(usuario)
                .produtos(produtos)
                .dataCompra(LocalDateTime.now())
                .valorTotal(calcularValorTotal(produtos))
                .endereco(dto.getEndereco())
                .cidade(dto.getCidade())
                .uf(dto.getUf())
                .formaPagamento(dto.getFormaPagamento())
                .build();

        return compraRepository.save(compra);
    }

    public List<Compra> listarTodas() {
        return compraRepository.findAll();
    }

    public List<Compra> listarPorCpf(String cpf) {
        return compraRepository.findByUsuarioCpf(cpf);
    }
}
