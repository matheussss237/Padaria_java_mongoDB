package com.matheushenrique.cadastro_usuario.controller;

import com.matheushenrique.cadastro_usuario.business.CompraService;
import com.matheushenrique.cadastro_usuario.dto.CompraRequestDTO;
import com.matheushenrique.cadastro_usuario.infrastructure.entitys.Compra;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @PostMapping
    public ResponseEntity<Compra>criarCompra(@RequestBody @Valid CompraRequestDTO dto, Authentication auth){
        Compra novaCompra = compraService.criarCompraComDTO(auth.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCompra);
    }

    @GetMapping
    public ResponseEntity<List<Compra>> listarTodas() {
        return ResponseEntity.ok(compraService.listarTodas());
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<Compra>> minhasCompras(Authentication auth) {
        return ResponseEntity.ok(compraService.listarPorCpf(auth.getName()));
    }
}
