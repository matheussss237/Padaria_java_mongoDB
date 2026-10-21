package com.matheushenrique.cadastro_usuario.infrastructure.repository;

import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Compra;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CompraRepository extends MongoRepository<Compra, String> {

    List<Compra> findByUsuarioCpf(String cpf);

    void deleteByUsuarioCpf(String cpf);
}
