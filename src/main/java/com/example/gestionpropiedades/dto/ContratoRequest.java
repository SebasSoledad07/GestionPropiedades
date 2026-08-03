package com.example.gestionpropiedades.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de entrada para crear o actualizar un contrato.
 */
public record ContratoRequest(
        @NotNull(message = "El id de la propiedad es obligatorio")
        Long propiedadId,

        @NotNull(message = "El id del inquilino es obligatorio")
        Long inquilinoId,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        LocalDate fechaFin,

        @NotNull(message = "La renta mensual es obligatoria")
        @DecimalMin(value = "0.0", inclusive = false, message = "La renta mensual debe ser mayor que 0")
        BigDecimal rentaMensual
) {
}
