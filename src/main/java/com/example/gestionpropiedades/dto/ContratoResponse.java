package com.example.gestionpropiedades.dto;

import com.example.gestionpropiedades.entity.enums.EstadoContrato;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de salida de un contrato.
 */
public record ContratoResponse(
        Long id,
        Long propiedadId,
        Long inquilinoId,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal rentaMensual,
        EstadoContrato estado
) {
}
