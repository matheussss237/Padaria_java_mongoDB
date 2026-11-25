package com.matheushenrique.cadastro_usuario.business;

import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Usuario;
import com.matheushenrique.cadastro_usuario.infrastructure.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void salvarUsuario(Usuario usuario) {
        repository.save(usuario);
    }

    public Usuario buscarUsuarioPorCpf(String cpf) {
        return repository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Cpf não encontrado"));
    }

    public void deletarUsuarioPorCpf(String cpf) {
        repository.deleteByCpf(cpf);
    }

    public Usuario atualizarUsuario(String cpf, Usuario usuario) {
        Usuario usuarioEntity = repository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Usuario usuarioAtualizado = Usuario.builder()
                .cpf(usuarioEntity.getCpf())
                .nome(usuario.getNome() != null ? usuario.getNome() : usuarioEntity.getNome())
                .email(usuario.getEmail() != null ? usuario.getEmail() : usuarioEntity.getEmail())
                .dataNascimento(usuario.getDataNascimento() != null ? usuario.getDataNascimento() : usuarioEntity.getDataNascimento())
                .telefone(usuario.getTelefone() != null ? usuario.getTelefone() : usuarioEntity.getTelefone())
                .build();

        repository.save(usuarioAtualizado);
        return usuarioEntity;
    }
    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

}
