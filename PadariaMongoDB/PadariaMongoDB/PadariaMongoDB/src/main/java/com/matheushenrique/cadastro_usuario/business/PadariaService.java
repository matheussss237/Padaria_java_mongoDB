package com.matheushenrique.cadastro_usuario.business;

import com.matheushenrique.cadastro_usuario.exceptions.ResourceNotFoundException;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Padaria;
import com.matheushenrique.cadastro_usuario.infrastructure.repository.PadariaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PadariaService {

    private final PadariaRepository repository;

    public PadariaService(PadariaRepository repository) {
        this.repository = repository;
    }

    public Padaria cadastrarProduto(Padaria padaria) {
        padaria.setId(null);
        return repository.save(padaria);
    }

    public List<Padaria> listarProdutos() {
        return repository.findAll();
    }

    public Padaria buscarProdutoPorId(String id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrado")
        );
    }

    public void deletarProdutoPorId(String id) {
        buscarProdutoPorId(id);
        repository.deleteById(id);
    }

    public Padaria atualizarProdutoPorId(String id, Padaria padaria) {
        Padaria padariaEntity = buscarProdutoPorId(id);
        Padaria produtoAtualizado = Padaria.builder()
                .id(padariaEntity.getId())
                .nome(padaria.getNome() != null ? padaria.getNome() :
                        padariaEntity.getNome())
                .preco(padaria.getPreco() != null ? padaria.getPreco() :
                        padariaEntity.getPreco())
                .descricao(padaria.getDescricao() != null ? padaria.getDescricao() :
                        padariaEntity.getDescricao())
                .categoria(padaria.getCategoria() != null ? padaria.getCategoria() :
                        padariaEntity.getCategoria())
                .imagem(padaria.getImagem() != null ? padaria.getImagem() :
                        padariaEntity.getImagem()).build();
        return repository.save(produtoAtualizado);
    }
}
