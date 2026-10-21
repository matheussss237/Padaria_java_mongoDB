package com.matheushenrique.cadastro_usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// Cadastro de cliente: cria o Usuario (dados pessoais) e o User (login = CPF)
public record RegisterDTO(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotBlank @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos") String cpf,
        @Email(message = "E-mail inválido") String email,
        LocalDate dataNascimento,
        String telefone,
        @NotBlank @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres") String senha) {
}
