package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.PropiedadRequest;
import com.example.gestionpropiedades.entity.Propiedad;
import com.example.gestionpropiedades.entity.enums.EstadoPropiedad;
import com.example.gestionpropiedades.exception.PropiedadNotFoundException;
import com.example.gestionpropiedades.repository.PropiedadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropiedadServiceTest {

    @Mock
    private PropiedadRepository propiedadRepository;

    @InjectMocks
    private PropiedadService propiedadService;

    private PropiedadRequest request() {
        return new PropiedadRequest("Calle 1", "Madrid", new BigDecimal("500.00"), 3, 2, "Casa");
    }

    @Test
    void findAllDevuelveTodasLasPropiedades() {
        when(propiedadRepository.findAll()).thenReturn(List.of(
                Propiedad.builder().id(1L).direccion("Calle 1").ciudad("Madrid").build(),
                Propiedad.builder().id(2L).direccion("Calle 2").ciudad("Barcelona").build()));

        assertThat(propiedadService.findAll()).hasSize(2);
    }

    @Test
    void findByIdInexistenteLanzaExcepcion() {
        when(propiedadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propiedadService.findById(99L))
                .isInstanceOf(PropiedadNotFoundException.class);
    }

    @Test
    void createPersistePropiedadDisponible() {
        Propiedad guardada = Propiedad.builder()
                .id(1L).direccion("Calle 1").ciudad("Madrid")
                .precioAlquiler(new BigDecimal("500.00"))
                .habitaciones(3).banos(2).descripcion("Casa")
                .build();
        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(guardada);

        var response = propiedadService.create(request());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.estado()).isEqualTo(EstadoPropiedad.DISPONIBLE);
        verify(propiedadRepository).save(any(Propiedad.class));
    }

    @Test
    void updateModificaLosCampos() {
        Propiedad existente = Propiedad.builder()
                .id(1L).direccion("Vieja").ciudad("Madrid").build();
        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(existente));

        var response = propiedadService.update(1L, request());

        assertThat(response.direccion()).isEqualTo("Calle 1");
        assertThat(existente.getCiudad()).isEqualTo("Madrid");
    }

    @Test
    void deleteEliminaPropiedadExistente() {
        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(
                Propiedad.builder().id(1L).build()));

        propiedadService.delete(1L);

        verify(propiedadRepository).deleteById(1L);
    }
}
