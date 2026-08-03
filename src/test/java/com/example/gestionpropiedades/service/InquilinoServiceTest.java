package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.InquilinoRequest;
import com.example.gestionpropiedades.entity.Inquilino;
import com.example.gestionpropiedades.exception.InquilinoNotFoundException;
import com.example.gestionpropiedades.exception.RecursoDuplicadoException;
import com.example.gestionpropiedades.repository.InquilinoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InquilinoServiceTest {

    @Mock
    private InquilinoRepository inquilinoRepository;

    @InjectMocks
    private InquilinoService inquilinoService;

    private InquilinoRequest request() {
        return new InquilinoRequest("Juan", "Perez", "12345678", "juan@mail.com", "555-1234");
    }

    @Test
    void createConDniDuplicadoLanzaExcepcion() {
        when(inquilinoRepository.findByDni("12345678"))
                .thenReturn(Optional.of(Inquilino.builder().id(5L).dni("12345678").build()));

        assertThatThrownBy(() -> inquilinoService.create(request()))
                .isInstanceOf(RecursoDuplicadoException.class);
    }

    @Test
    void createConEmailDuplicadoLanzaExcepcion() {
        when(inquilinoRepository.findByDni("12345678")).thenReturn(Optional.empty());
        when(inquilinoRepository.findByEmail("juan@mail.com"))
                .thenReturn(Optional.of(Inquilino.builder().id(5L).email("juan@mail.com").build()));

        assertThatThrownBy(() -> inquilinoService.create(request()))
                .isInstanceOf(RecursoDuplicadoException.class);
    }

    @Test
    void updateConDniPropioNoLanzaExcepcion() {
        Inquilino existente = Inquilino.builder().id(5L).dni("12345678").email("juan@mail.com").build();
        when(inquilinoRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(inquilinoRepository.findByDni("12345678")).thenReturn(Optional.of(existente));

        inquilinoService.update(5L, request());

        verify(inquilinoRepository).findById(5L);
    }

    @Test
    void findByIdInexistenteLanzaExcepcion() {
        when(inquilinoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inquilinoService.findById(99L))
                .isInstanceOf(InquilinoNotFoundException.class);
    }
}
