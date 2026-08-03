package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.UsuarioRequest;
import com.example.gestionpropiedades.entity.Usuario;
import com.example.gestionpropiedades.entity.enums.Rol;
import com.example.gestionpropiedades.exception.RecursoDuplicadoException;
import com.example.gestionpropiedades.exception.UsuarioNotFoundException;
import com.example.gestionpropiedades.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequest request() {
        return new UsuarioRequest("Admin", "admin@mail.com", "password123", Rol.ADMINISTRADOR);
    }

    @Test
    void createNoExponeLaContrasena() {
        Usuario guardado = Usuario.builder().id(1L).nombre("Admin")
                .email("admin@mail.com").password("password123").rol(Rol.ADMINISTRADOR).build();
        when(usuarioRepository.findByEmail("admin@mail.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(org.mockito.ArgumentMatchers.any(Usuario.class))).thenReturn(guardado);

        var response = usuarioService.create(request());

        assertThat(response.rol()).isEqualTo(Rol.ADMINISTRADOR);
        assertThat(response).hasNoNullFieldsOrPropertiesExcept();
        assertThat(response.toString()).doesNotContain("password");
    }

    @Test
    void createConEmailDuplicadoLanzaExcepcion() {
        when(usuarioRepository.findByEmail("admin@mail.com"))
                .thenReturn(Optional.of(Usuario.builder().id(5L).email("admin@mail.com").build()));

        assertThatThrownBy(() -> usuarioService.create(request()))
                .isInstanceOf(RecursoDuplicadoException.class);
    }

    @Test
    void findByIdInexistenteLanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.findById(99L))
                .isInstanceOf(UsuarioNotFoundException.class);
    }
}
