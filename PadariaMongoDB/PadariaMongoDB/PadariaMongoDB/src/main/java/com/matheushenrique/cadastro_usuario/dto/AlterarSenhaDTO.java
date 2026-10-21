package com.matheushenrique.cadastro_usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaDTO(
        @NotBlank @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres") String senha) {
}
