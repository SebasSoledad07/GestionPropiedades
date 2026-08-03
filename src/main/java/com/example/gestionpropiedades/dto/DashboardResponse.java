package com.example.gestionpropiedades.dto;

import java.math.BigDecimal;

/**
 * Resumen del dashboard de gestión.
 *
 * @param ingresosTotales suma de todos los pagos en estado {@code PAGADO}
 * @param contratosActivos número de contratos en estado {@code ACTIVO}
 * @param pagosPendientes número de pagos en estado {@code PENDIENTE} o {@code VENCIDO}
 * @param montoPendiente suma de los montos de los pagos pendientes o vencidos
 */
public record DashboardResponse(
        BigDecimal ingresosTotales,
        long contratosActivos,
        long pagosPendientes,
        BigDecimal montoPendiente
) {
}
