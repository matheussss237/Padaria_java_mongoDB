package com.matheushenrique.cadastro_usuario.business;


import com.matheushenrique.cadastro_usuario.exceptions.RegraNegocioException;
import com.matheushenrique.cadastro_usuario.exceptions.ResourceNotFoundException;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Usuario;
import com.matheushenrique.cadastro_usuario.infrastructure.repository.CompraRepository;
import com.matheushenrique.cadastro_usuario.infrastructure.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final CompraRepository compraRepository;
    private final AuthorizationService authorizationService;

    public UsuarioService(UsuarioRepository repository, CompraRepository compraRepository,
                          AuthorizationService authorizationService) {
        this.repository = repository;
        this.compraRepository = compraRepository;
        this.authorizationService = authorizationService;
    }

    public Usuario salvarUsuario(Usuario usuario) {
        if (usuario.getCpf() == null || usuario.getCpf().isBlank()) {
            throw new RegraNegocioException("CPF é obrigatório");
        }
        if (repository.findByCpf(usuario.getCpf()).isPresent()) {
            throw new RegraNegocioException("Já existe um usuário com esse CPF");
        }
        usuario.setId(null);
        return repository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario buscarUsuarioPorCpf(String cpf) {
        return repository.findByCpf(cpf).orElseThrow(
                ()-> new ResourceNotFoundException("Cpf não encontrado")
        );
    }

    // Remove também as compras e o login ligado a esse CPF
    public void deletarUsuarioPorCpf(String cpf) {
        buscarUsuarioPorCpf(cpf);
        compraRepository.deleteByUsuarioCpf(cpf);
        repository.deleteByCpf(cpf);
        authorizationService.removerLogin(cpf);
    }

    public Usuario atualizarUsuarioporCpf(String cpf, Usuario usuario) {
        Usuario usuarioEntity = buscarUsuarioPorCpf(cpf);
        Usuario usuarioAtualizado = Usuario.builder()
                .id(usuarioEntity.getId())
                .nome(usuario.getNome()!=null ? usuario.getNome():
                        usuarioEntity.getNome())
                .cpf(usuarioEntity.getCpf())
                .email(usuario.getEmail()!=null ? usuario.getEmail() :
                        usuarioEntity.getEmail())
                .dataNascimento(usuario.getDataNascimento()!= null ? usuario.getDataNascimento():
                        usuarioEntity.getDataNascimento())
                .telefone(usuario.getTelefone()!= null ? usuario.getTelefone() :
                        usuarioEntity.getTelefone()).build();
        return repository.save(usuarioAtualizado);
    }
}
