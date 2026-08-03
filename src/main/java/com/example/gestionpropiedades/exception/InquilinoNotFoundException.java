package com.example.gestionpropiedades.exception;

/**
 * Lanzada cuando no se encuentra un inquilino.
 */
public class InquilinoNotFoundException extends ResourceNotFoundException {

    public InquilinoNotFoundException(Long id) {
        super("Inquilino con id " + id + " no encontrado");
    }
}
