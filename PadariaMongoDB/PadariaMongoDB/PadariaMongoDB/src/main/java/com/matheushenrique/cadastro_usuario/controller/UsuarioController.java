package com.matheushenrique.cadastro_usuario.controller;

import com.matheushenrique.cadastro_usuario.business.UsuarioService;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://padaria-java.onrender.com/usuarios")

public class UsuarioController {

    private final UsuarioService usuarioService;
    @GetMapping("/{cpf}")
    public Usuario buscarUsuarioPorCpf(@PathVariable String cpf) {
        return usuarioService.buscarUsuarioPorCpf(cpf);
    }

    @PostMapping
    public ResponseEntity <Void> salvarUsuario(@RequestBody Usuario usuario) {
            usuarioService.salvarUsuario(usuario);
            return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity <Void> deletarUsuarioPorCpf(@PathVariable String cpf) {
        usuarioService.deletarUsuarioPorCpf(cpf);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable String cpf, @RequestBody Usuario usuario) {
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(cpf, usuario);
        return ResponseEntity.ok(usuarioAtualizado); // Retorna o usuário atualizado com status 200
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }
}

