package com.example.gestionpropiedades.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Datos de entrada para crear o actualizar una propiedad.
 */
public record PropiedadRequest(
        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
        String direccion,

        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
        String ciudad,

        @NotNull(message = "El precio de alquiler es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio de alquiler debe ser mayor que 0")
        BigDecimal precioAlquiler,

        @NotNull(message = "El número de habitaciones es obligatorio")
        @Min(value = 0, message = "El número de habitaciones no puede ser negativo")
        Integer habitaciones,

        @NotNull(message = "El número de baños es obligatorio")
        @Min(value = 0, message = "El número de baños no puede ser negativo")
        Integer banos,

        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        String descripcion
) {
}
