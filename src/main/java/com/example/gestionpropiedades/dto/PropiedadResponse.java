package com.example.gestionpropiedades.dto;

import com.example.gestionpropiedades.entity.enums.EstadoPropiedad;

import java.math.BigDecimal;

/**
 * Datos de salida de una propiedad.
 */
public record PropiedadResponse(
        Long id,
        String direccion,
        String ciudad,
        BigDecimal precioAlquiler,
        Integer habitaciones,
        Integer banos,
        EstadoPropiedad estado,
        String descripcion
) {
}
