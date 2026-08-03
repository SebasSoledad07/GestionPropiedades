package com.example.gestionpropiedades.dto;

import com.example.gestionpropiedades.entity.enums.EstadoPago;
import com.example.gestionpropiedades.entity.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de salida de un pago.
 */
public record PagoResponse(
        Long id,
        Long contratoId,
        BigDecimal monto,
        LocalDate fechaPago,
        String periodo,
        MetodoPago metodoPago,
        EstadoPago estado
) {
}
