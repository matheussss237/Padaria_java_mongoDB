package com.matheushenrique.cadastro_usuario.business;

import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Padaria;
import com.matheushenrique.cadastro_usuario.infrastructure.repository.PadariaRepository;
import org.springframework.stereotype.Service;

@Service
public class PadariaService {

    private final PadariaRepository repository;

    public PadariaService(PadariaRepository repository) {
        this.repository = repository;
    }

    public void cadastrarProduto(Padaria padaria) {
        repository.save(padaria); // Mongo usa só save
    }

    public Padaria buscarProdutoPorId(String id) { // ID como String
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public void deletarProdutoPorId(String id) { // ID como String
        repository.deleteById(id);
    }

    public void atualizarProdutoPorId(String id, Padaria padaria) {
        Padaria padariaEntity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Padaria produtoAtualizado = Padaria.builder()
                .id(padariaEntity.getId()) // mantém mesmo ID do Mongo
                .nome(padaria.getNome() != null ? padaria.getNome() : padariaEntity.getNome())
                .preco(padaria.getPreco() != null ? padaria.getPreco() : padariaEntity.getPreco())
                .build();

        repository.save(produtoAtualizado); // Mongo usa save
    }
}

