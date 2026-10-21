package com.matheushenrique.cadastro_usuario.infrastructure.repository;

import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByCpf(String cpf);

    void deleteByCpf(String cpf);
}
