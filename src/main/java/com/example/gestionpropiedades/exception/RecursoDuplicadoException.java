package com.example.gestionpropiedades.exception;

/**
 * Lanzada cuando un recurso intenta duplicar un valor único.
 */
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String message) {
        super(message);
    }
}
