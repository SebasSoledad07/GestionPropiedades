package com.example.gestionpropiedades.controller;

import com.example.gestionpropiedades.dto.ContratoRequest;
import com.example.gestionpropiedades.dto.ContratoResponse;
import com.example.gestionpropiedades.service.ContratoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de contratos.
 */
@RestController
@RequestMapping("/api/contratos")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @GetMapping
    public List<ContratoResponse> findAll() {
        return contratoService.findAll();
    }

    @GetMapping("/{id}")
    public ContratoResponse findById(@PathVariable Long id) {
        return contratoService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ContratoResponse> create(@Valid @RequestBody ContratoRequest request) {
        ContratoResponse response = contratoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ContratoResponse update(@PathVariable Long id, @Valid @RequestBody ContratoRequest request) {
        return contratoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contratoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
