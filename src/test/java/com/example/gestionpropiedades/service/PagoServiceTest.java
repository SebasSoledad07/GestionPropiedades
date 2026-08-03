package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.PagoRequest;
import com.example.gestionpropiedades.entity.Contrato;
import com.example.gestionpropiedades.entity.Pago;
import com.example.gestionpropiedades.entity.enums.MetodoPago;
import com.example.gestionpropiedades.exception.PagoDuplicadoException;
import com.example.gestionpropiedades.repository.ContratoRepository;
import com.example.gestionpropiedades.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private ContratoRepository contratoRepository;

    @InjectMocks
    private PagoService pagoService;

    private PagoRequest request() {
        return new PagoRequest(1L, new BigDecimal("500.00"),
                LocalDate.of(2025, 1, 5), "2025-01", MetodoPago.TRANSFERENCIA);
    }

    @Test
    void createConPeriodoDuplicadoLanzaExcepcion() {
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(Contrato.builder().id(1L).build()));
        when(pagoRepository.existsByContratoIdAndPeriodo(1L, "2025-01")).thenReturn(true);

        assertThatThrownBy(() -> pagoService.create(request()))
                .isInstanceOf(PagoDuplicadoException.class);
    }

    @Test
    void createValidoPersisteElPago() {
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(Contrato.builder().id(1L).build()));
        when(pagoRepository.existsByContratoIdAndPeriodo(1L, "2025-01")).thenReturn(false);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pagoService.create(request());

        verify(pagoRepository).save(any(Pago.class));
    }
}
