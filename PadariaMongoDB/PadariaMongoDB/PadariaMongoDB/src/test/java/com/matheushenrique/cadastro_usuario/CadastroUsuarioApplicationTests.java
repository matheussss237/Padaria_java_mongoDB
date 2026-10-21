package com.matheushenrique.cadastro_usuario;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class CadastroUsuarioApplicationTests {

    @DynamicPropertySource
    static void mongo(DynamicPropertyRegistry registry) {
        MongoEmMemoria.registrar(registry);
    }

	@Test
	void contextLoads() {
	}

}
