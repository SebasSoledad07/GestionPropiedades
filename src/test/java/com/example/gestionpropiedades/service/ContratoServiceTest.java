package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.ContratoRequest;
import com.example.gestionpropiedades.entity.Contrato;
import com.example.gestionpropiedades.entity.Inquilino;
import com.example.gestionpropiedades.entity.Propiedad;
import com.example.gestionpropiedades.entity.enums.EstadoContrato;
import com.example.gestionpropiedades.entity.enums.EstadoPropiedad;
import com.example.gestionpropiedades.exception.ContratoActivoException;
import com.example.gestionpropiedades.repository.ContratoRepository;
import com.example.gestionpropiedades.repository.InquilinoRepository;
import com.example.gestionpropiedades.repository.PropiedadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContratoServiceTest {

    @Mock
    private ContratoRepository contratoRepository;

    @Mock
    private PropiedadRepository propiedadRepository;

    @Mock
    private InquilinoRepository inquilinoRepository;

    @InjectMocks
    private ContratoService contratoService;

    private Propiedad propiedad;
    private Inquilino inquilino;

    @BeforeEach
    void setUp() {
        propiedad = Propiedad.builder().id(1L).estado(EstadoPropiedad.DISPONIBLE).build();
        inquilino = Inquilino.builder().id(1L).build();
    }

    private ContratoRequest request() {
        return new ContratoRequest(1L, 1L,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                new BigDecimal("500.00"));
    }

    @Test
    void createMarcaLaPropiedadComoOcupada() {
        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedad));
        when(inquilinoRepository.findById(1L)).thenReturn(Optional.of(inquilino));
        when(contratoRepository.existsByPropiedadIdAndEstado(1L, EstadoContrato.ACTIVO)).thenReturn(false);
        when(contratoRepository.save(any(Contrato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        contratoService.create(request());

        assertThat(propiedad.getEstado()).isEqualTo(EstadoPropiedad.OCUPADA);
    }

    @Test
    void createConContratoActivoExistenteLanzaExcepcion() {
        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedad));
        when(inquilinoRepository.findById(1L)).thenReturn(Optional.of(inquilino));
        when(contratoRepository.existsByPropiedadIdAndEstado(1L, EstadoContrato.ACTIVO)).thenReturn(true);

        assertThatThrownBy(() -> contratoService.create(request()))
                .isInstanceOf(ContratoActivoException.class);
    }

    @Test
    void finalizarLiberaLaPropiedad() {
        Propiedad ocupada = Propiedad.builder().id(1L).estado(EstadoPropiedad.OCUPADA).build();
        Contrato contrato = Contrato.builder()
                .id(1L).propiedad(ocupada).inquilino(inquilino)
                .estado(EstadoContrato.ACTIVO).build();
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));
        when(contratoRepository.existsByPropiedadIdAndEstadoAndIdNot(1L, EstadoContrato.ACTIVO, 1L))
                .thenReturn(false);

        contratoService.finalizar(1L);

        assertThat(contrato.getEstado()).isEqualTo(EstadoContrato.FINALIZADO);
        assertThat(ocupada.getEstado()).isEqualTo(EstadoPropiedad.DISPONIBLE);
    }

    @Test
    void activarConOtroContratoActivoLanzaExcepcion() {
        Contrato contrato = Contrato.builder()
                .id(1L).propiedad(propiedad).inquilino(inquilino)
                .estado(EstadoContrato.CANCELADO).build();
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));
        when(contratoRepository.existsByPropiedadIdAndEstadoAndIdNot(1L, EstadoContrato.ACTIVO, 1L))
                .thenReturn(true);

        assertThatThrownBy(() -> contratoService.activar(1L))
                .isInstanceOf(ContratoActivoException.class);
    }
}
