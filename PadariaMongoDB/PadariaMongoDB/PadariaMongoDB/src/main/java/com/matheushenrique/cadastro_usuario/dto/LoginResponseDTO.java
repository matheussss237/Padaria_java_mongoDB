package com.matheushenrique.cadastro_usuario.dto;

import com.matheushenrique.cadastro_usuario.role.UserRole;

public record LoginResponseDTO(String login, UserRole role, String nome) {
}
