package com.example.gestionpropiedades.dto;

/**
 * Datos de salida de un inquilino.
 */
public record InquilinoResponse(
        Long id,
        String nombre,
        String apellido,
        String dni,
        String email,
        String telefono
) {
}
