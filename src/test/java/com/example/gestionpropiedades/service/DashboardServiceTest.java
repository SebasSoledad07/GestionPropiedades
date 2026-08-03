package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.DashboardResponse;
import com.example.gestionpropiedades.entity.enums.EstadoContrato;
import com.example.gestionpropiedades.entity.enums.EstadoPago;
import com.example.gestionpropiedades.repository.ContratoRepository;
import com.example.gestionpropiedades.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private ContratoRepository contratoRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void obtenerResumenCalculaLasMetricas() {
        when(contratoRepository.countByEstado(EstadoContrato.ACTIVO)).thenReturn(3L);
        when(pagoRepository.countByEstado(EstadoPago.PENDIENTE)).thenReturn(2L);
        when(pagoRepository.countByEstado(EstadoPago.VENCIDO)).thenReturn(1L);
        when(pagoRepository.sumMontoByEstado(EstadoPago.PAGADO)).thenReturn(new BigDecimal("1500.00"));
        when(pagoRepository.sumMontoByEstado(EstadoPago.PENDIENTE)).thenReturn(new BigDecimal("400.00"));
        when(pagoRepository.sumMontoByEstado(EstadoPago.VENCIDO)).thenReturn(new BigDecimal("100.00"));

        DashboardResponse resumen = dashboardService.obtenerResumen();

        assertThat(resumen.contratosActivos()).isEqualTo(3);
        assertThat(resumen.pagosPendientes()).isEqualTo(3);
        assertThat(resumen.ingresosTotales()).isEqualByComparingTo("1500.00");
        assertThat(resumen.montoPendiente()).isEqualByComparingTo("500.00");
    }
}
