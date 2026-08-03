package com.example.gestionpropiedades.dto;

import com.example.gestionpropiedades.entity.enums.Rol;

/**
 * Datos de salida de un usuario.
 */
public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        Rol rol
) {
}
