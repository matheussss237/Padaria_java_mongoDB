package com.matheushenrique.cadastro_usuario.business;

import com.matheushenrique.cadastro_usuario.dto.RegisterDTO;
import com.matheushenrique.cadastro_usuario.exceptions.RegraNegocioException;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.User;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Usuario;
import com.matheushenrique.cadastro_usuario.infrastructure.repository.UserRepository;
import com.matheushenrique.cadastro_usuario.infrastructure.repository.UsuarioRepository;
import com.matheushenrique.cadastro_usuario.role.UserRole;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    private final UserRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthorizationService(UserRepository repository, UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
        return user;
    }

    public boolean userExists(String login) {
        return repository.findByUsername(login) != null;
    }

    // Cadastro público: sempre cria com perfil USER (admin não pode ser criado pela tela)
    public Usuario registrarCliente(RegisterDTO data) {
        if (userExists(data.cpf()) || usuarioRepository.findByCpf(data.cpf()).isPresent()) {
            throw new RegraNegocioException("Já existe um cadastro com esse CPF");
        }

        Usuario usuario = Usuario.builder()
                .nome(data.nome())
                .cpf(data.cpf())
                .email(data.email())
                .dataNascimento(data.dataNascimento())
                .telefone(data.telefone())
                .build();
        usuarioRepository.save(usuario);

        registerUser(data.cpf(), data.senha(), UserRole.USER);
        return usuario;
    }

    public void registerUser(String login, String password, UserRole role) {
        if (userExists(login)) {
            throw new RegraNegocioException("Login já existe");
        }
        repository.save(new User(login, passwordEncoder.encode(password), role));
    }

    public void alterarSenha(String login, String novaSenha) {
        User user = (User) loadUserByUsername(login);
        user.setPassword(passwordEncoder.encode(novaSenha));
        repository.save(user);
    }

    public void removerLogin(String login) {
        repository.deleteByUsername(login);
    }

    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }

}
