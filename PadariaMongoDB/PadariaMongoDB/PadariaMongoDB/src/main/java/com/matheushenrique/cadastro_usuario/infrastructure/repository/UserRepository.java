package com.matheushenrique.cadastro_usuario.infrastructure.repository;

import com.matheushenrique.cadastro_usuario.infrastructure.entitys.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);

    void deleteByUsername(String username);
}
