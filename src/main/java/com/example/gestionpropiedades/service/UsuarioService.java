package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.UsuarioRequest;
import com.example.gestionpropiedades.dto.UsuarioResponse;
import com.example.gestionpropiedades.entity.Usuario;
import com.example.gestionpropiedades.exception.RecursoDuplicadoException;
import com.example.gestionpropiedades.exception.UsuarioNotFoundException;
import com.example.gestionpropiedades.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para usuarios.
 */
@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        return toResponse(getUsuarioOrThrow(id));
    }

    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        validateEmailUnique(request.email(), null);
        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .email(request.email())
                .password(request.password())
                .rol(request.rol())
                .build();
        Usuario saved = usuarioRepository.save(usuario);
        log.info("create_usuario id={}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public UsuarioResponse update(Long id, UsuarioRequest request) {
        Usuario usuario = getUsuarioOrThrow(id);
        validateEmailUnique(request.email(), id);
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPassword(request.password());
        usuario.setRol(request.rol());
        log.info("update_usuario id={}", id);
        return toResponse(usuario);
    }

    @Transactional
    public void delete(Long id) {
        getUsuarioOrThrow(id);
        usuarioRepository.deleteById(id);
        log.info("delete_usuario id={}", id);
    }

    private void validateEmailUnique(String email, Long currentId) {
        usuarioRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new RecursoDuplicadoException("Ya existe un usuario con el email " + email);
                });
    }

    private Usuario getUsuarioOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
    }

    private static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol()
        );
    }
}
