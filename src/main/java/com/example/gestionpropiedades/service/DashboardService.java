package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.DashboardResponse;
import com.example.gestionpropiedades.entity.enums.EstadoContrato;
import com.example.gestionpropiedades.entity.enums.EstadoPago;
import com.example.gestionpropiedades.repository.ContratoRepository;
import com.example.gestionpropiedades.repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Servicio que agrega métricas de negocio para el dashboard.
 */
@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final PagoRepository pagoRepository;
    private final ContratoRepository contratoRepository;

    public DashboardService(PagoRepository pagoRepository, ContratoRepository contratoRepository) {
        this.pagoRepository = pagoRepository;
        this.contratoRepository = contratoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse obtenerResumen() {
        long contratosActivos = contratoRepository.countByEstado(EstadoContrato.ACTIVO);
        long pagosPendientes = pagoRepository.countByEstado(EstadoPago.PENDIENTE)
                + pagoRepository.countByEstado(EstadoPago.VENCIDO);

        BigDecimal ingresosTotales = pagoRepository.sumMontoByEstado(EstadoPago.PAGADO);
        BigDecimal montoPendiente = pagoRepository.sumMontoByEstado(EstadoPago.PENDIENTE)
                .add(pagoRepository.sumMontoByEstado(EstadoPago.VENCIDO));

        log.info("dashboard_resumen contratos_activos={} pagos_pendientes={}", contratosActivos, pagosPendientes);
        return new DashboardResponse(ingresosTotales, contratosActivos, pagosPendientes, montoPendiente);
    }
}
