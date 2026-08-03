package com.example.gestionpropiedades.controller;

import com.example.gestionpropiedades.dto.InquilinoRequest;
import com.example.gestionpropiedades.dto.InquilinoResponse;
import com.example.gestionpropiedades.service.InquilinoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de inquilinos.
 */
@RestController
@RequestMapping("/api/inquilinos")
public class InquilinoController {

    private final InquilinoService inquilinoService;

    public InquilinoController(InquilinoService inquilinoService) {
        this.inquilinoService = inquilinoService;
    }

    @GetMapping
    public List<InquilinoResponse> findAll() {
        return inquilinoService.findAll();
    }

    @GetMapping("/{id}")
    public InquilinoResponse findById(@PathVariable Long id) {
        return inquilinoService.findById(id);
    }

    @PostMapping
    public ResponseEntity<InquilinoResponse> create(@Valid @RequestBody InquilinoRequest request) {
        InquilinoResponse response = inquilinoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public InquilinoResponse update(@PathVariable Long id, @Valid @RequestBody InquilinoRequest request) {
        return inquilinoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inquilinoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
