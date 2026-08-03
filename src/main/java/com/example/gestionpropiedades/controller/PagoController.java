package com.example.gestionpropiedades.controller;

import com.example.gestionpropiedades.dto.PagoRequest;
import com.example.gestionpropiedades.dto.PagoResponse;
import com.example.gestionpropiedades.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de pagos.
 */
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public List<PagoResponse> findAll() {
        return pagoService.findAll();
    }

    @GetMapping("/{id}")
    public PagoResponse findById(@PathVariable Long id) {
        return pagoService.findById(id);
    }

    @GetMapping("/contrato/{contratoId}")
    public List<PagoResponse> findByContratoId(@PathVariable Long contratoId) {
        return pagoService.findByContratoId(contratoId);
    }

    @PostMapping
    public ResponseEntity<PagoResponse> create(@Valid @RequestBody PagoRequest request) {
        PagoResponse response = pagoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public PagoResponse update(@PathVariable Long id, @Valid @RequestBody PagoRequest request) {
        return pagoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pagoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
