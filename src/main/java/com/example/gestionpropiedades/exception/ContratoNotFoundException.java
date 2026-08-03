package com.example.gestionpropiedades.exception;

/**
 * Lanzada cuando no se encuentra un contrato.
 */
public class ContratoNotFoundException extends ResourceNotFoundException {

    public ContratoNotFoundException(Long id) {
        super("Contrato con id " + id + " no encontrado");
    }
}
