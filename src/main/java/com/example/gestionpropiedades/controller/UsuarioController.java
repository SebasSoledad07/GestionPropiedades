package com.example.gestionpropiedades.controller;

import com.example.gestionpropiedades.dto.UsuarioRequest;
import com.example.gestionpropiedades.dto.UsuarioResponse;
import com.example.gestionpropiedades.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> findAll() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public UsuarioResponse findById(@PathVariable Long id) {
        return usuarioService.findById(id);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public UsuarioResponse update(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        return usuarioService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
