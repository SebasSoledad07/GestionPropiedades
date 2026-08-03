package com.example.gestionpropiedades.controller;

import com.example.gestionpropiedades.dto.PropiedadRequest;
import com.example.gestionpropiedades.dto.PropiedadResponse;
import com.example.gestionpropiedades.service.PropiedadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de propiedades.
 */
@RestController
@RequestMapping("/api/propiedades")
public class PropiedadController {

    private final PropiedadService propiedadService;

    public PropiedadController(PropiedadService propiedadService) {
        this.propiedadService = propiedadService;
    }

    @GetMapping
    public List<PropiedadResponse> findAll() {
        return propiedadService.findAll();
    }

    @GetMapping("/{id}")
    public PropiedadResponse findById(@PathVariable Long id) {
        return propiedadService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PropiedadResponse> create(@Valid @RequestBody PropiedadRequest request) {
        PropiedadResponse response = propiedadService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public PropiedadResponse update(@PathVariable Long id, @Valid @RequestBody PropiedadRequest request) {
        return propiedadService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        propiedadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
