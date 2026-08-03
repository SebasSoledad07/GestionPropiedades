package com.example.gestionpropiedades.dto;

import com.example.gestionpropiedades.entity.enums.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de entrada para crear un pago.
 */
public record PagoRequest(
        @NotNull(message = "El id del contrato es obligatorio")
        Long contratoId,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor que 0")
        BigDecimal monto,

        @NotNull(message = "La fecha de pago es obligatoria")
        LocalDate fechaPago,

        @NotBlank(message = "El período es obligatorio")
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "El período debe tener formato yyyy-MM")
        String periodo,

        @NotNull(message = "El método de pago es obligatorio")
        MetodoPago metodoPago
) {
}
