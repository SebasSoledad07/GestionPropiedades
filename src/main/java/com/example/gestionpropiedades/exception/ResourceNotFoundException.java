package com.example.gestionpropiedades.exception;

/**
 * Excepción base lanzada cuando un recurso no existe.
 */
public abstract class ResourceNotFoundException extends RuntimeException {

    protected ResourceNotFoundException(String message) {
        super(message);
    }
}
