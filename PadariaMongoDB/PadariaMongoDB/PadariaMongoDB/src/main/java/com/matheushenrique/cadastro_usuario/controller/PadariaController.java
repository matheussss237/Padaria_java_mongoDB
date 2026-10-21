package com.matheushenrique.cadastro_usuario.controller;

import com.matheushenrique.cadastro_usuario.business.PadariaService;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Padaria;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/padaria")
@RequiredArgsConstructor

public class PadariaController {

    private final PadariaService padariaService;

    @PostMapping
    public ResponseEntity <Padaria> cadastrarProduto (@RequestBody Padaria padaria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(padariaService.cadastrarProduto(padaria));
    }
    @GetMapping
    public ResponseEntity<List<Padaria>> listarProdutos () {
        return ResponseEntity.ok(padariaService.listarProdutos());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Padaria> buscarProdutoPorId (@PathVariable String id) {
        return ResponseEntity.ok(padariaService.buscarProdutoPorId(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProdutoPorId (@PathVariable String id) {
        padariaService.deletarProdutoPorId(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Padaria> atualizarPadaria (@PathVariable String id, @RequestBody Padaria padaria) {
        return ResponseEntity.ok(padariaService.atualizarProdutoPorId(id, padaria));
    }
}
