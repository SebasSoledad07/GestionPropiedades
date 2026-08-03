package com.example.gestionpropiedades.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Cuerpo de error JSON devuelto por el manejador global de excepciones.
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> errors
) {
    public ApiError(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }

    public ApiError(int status, String error, String message, String path, Map<String, String> errors) {
        this(LocalDateTime.now(), status, error, message, path, errors);
    }
}
