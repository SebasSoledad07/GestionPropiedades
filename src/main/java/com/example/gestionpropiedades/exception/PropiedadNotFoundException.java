package com.example.gestionpropiedades.exception;

/**
 * Lanzada cuando no se encuentra una propiedad.
 */
public class PropiedadNotFoundException extends ResourceNotFoundException {

    public PropiedadNotFoundException(Long id) {
        super("Propiedad con id " + id + " no encontrada");
    }
}
