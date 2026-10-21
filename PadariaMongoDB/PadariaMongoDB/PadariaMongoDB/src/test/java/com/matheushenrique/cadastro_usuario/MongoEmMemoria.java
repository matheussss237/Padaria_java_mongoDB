package com.matheushenrique.cadastro_usuario;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.springframework.test.context.DynamicPropertyRegistry;

// Sobe um servidor MongoDB em memória para os testes
final class MongoEmMemoria {

    private static MongoServer server;

    private MongoEmMemoria() {
    }

    static synchronized void registrar(DynamicPropertyRegistry registry) {
        if (server == null) {
            server = new MongoServer(new MemoryBackend());
            server.bind();
        }
        // banco novo a cada contexto para os testes não interferirem entre si
        String banco = "padaria" + System.nanoTime();
        registry.add("spring.data.mongodb.uri", () -> server.getConnectionString() + "/" + banco);
    }
}
