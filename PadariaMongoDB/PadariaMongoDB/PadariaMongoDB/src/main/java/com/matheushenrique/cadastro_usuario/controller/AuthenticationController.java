package com.matheushenrique.cadastro_usuario.controller;

import com.matheushenrique.cadastro_usuario.business.AuthorizationService;
import com.matheushenrique.cadastro_usuario.dto.AlterarSenhaDTO;
import com.matheushenrique.cadastro_usuario.dto.AuthenticationDTO;
import com.matheushenrique.cadastro_usuario.dto.LoginResponseDTO;
import com.matheushenrique.cadastro_usuario.dto.RegisterDTO;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.User;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Usuario;
import com.matheushenrique.cadastro_usuario.infrastructure.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final AuthorizationService authorizationService;
    private final UsuarioRepository usuarioRepository;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    AuthorizationService authorizationService,
                                    UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody @Valid RegisterDTO data) {
        Usuario usuario = authorizationService.registrarCliente(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    // Confere login e senha; o front depois manda as credenciais via HTTP Basic
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.login(), data.senha()));
        User user = (User) auth.getPrincipal();
        String nome = usuarioRepository.findByCpf(user.getUsername())
                .map(Usuario::getNome)
                .orElse(user.getUsername());
        return ResponseEntity.ok(new LoginResponseDTO(user.getUsername(), user.getRole(), nome));
    }

    @PutMapping("/senha")
    public ResponseEntity<Void> alterarSenha(@RequestBody @Valid AlterarSenhaDTO data, Authentication auth) {
        authorizationService.alterarSenha(auth.getName(), data.senha());
        return ResponseEntity.noContent().build();
    }
}
