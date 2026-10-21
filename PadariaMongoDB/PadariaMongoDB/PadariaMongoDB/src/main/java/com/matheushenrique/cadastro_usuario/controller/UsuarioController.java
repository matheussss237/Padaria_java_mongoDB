package com.matheushenrique.cadastro_usuario.controller;

import com.matheushenrique.cadastro_usuario.business.UsuarioService;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor

public class UsuarioController {

    private final UsuarioService usuarioService;

    // Cliente só pode mexer no próprio cadastro; admin pode mexer em todos
    private void verificarAcesso(String cpf, Authentication auth) {
        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !auth.getName().equals(cpf)) {
            throw new AccessDeniedException("Você só pode acessar o seu próprio cadastro");
        }
    }

    @PostMapping
    public ResponseEntity <Usuario> salvarUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.salvarUsuario(usuario));
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity <Usuario> buscarUsuarioPorCpf(@PathVariable String cpf, Authentication auth) {
        verificarAcesso(cpf, auth);
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorCpf(cpf));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity <Void> deletarUsuarioPorCpf(@PathVariable String cpf, Authentication auth) {
        verificarAcesso(cpf, auth);
        usuarioService.deletarUsuarioPorCpf(cpf);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{cpf}")
    public ResponseEntity <Usuario> atualizarUsuarioporCpf(@PathVariable String cpf,
                                                        @RequestBody Usuario usuario,
                                                        Authentication auth) {
        verificarAcesso(cpf, auth);
        return ResponseEntity.ok(usuarioService.atualizarUsuarioporCpf(cpf, usuario));
    }
}
