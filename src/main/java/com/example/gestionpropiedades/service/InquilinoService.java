package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.InquilinoRequest;
import com.example.gestionpropiedades.dto.InquilinoResponse;
import com.example.gestionpropiedades.entity.Inquilino;
import com.example.gestionpropiedades.exception.InquilinoNotFoundException;
import com.example.gestionpropiedades.exception.RecursoDuplicadoException;
import com.example.gestionpropiedades.repository.InquilinoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para inquilinos.
 */
@Service
public class InquilinoService {

    private static final Logger log = LoggerFactory.getLogger(InquilinoService.class);

    private final InquilinoRepository inquilinoRepository;

    public InquilinoService(InquilinoRepository inquilinoRepository) {
        this.inquilinoRepository = inquilinoRepository;
    }

    @Transactional(readOnly = true)
    public List<InquilinoResponse> findAll() {
        return inquilinoRepository.findAll().stream()
                .map(InquilinoService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InquilinoResponse findById(Long id) {
        return toResponse(getInquilinoOrThrow(id));
    }

    @Transactional
    public InquilinoResponse create(InquilinoRequest request) {
        validateUniques(request, null);
        Inquilino inquilino = Inquilino.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .dni(request.dni())
                .email(request.email())
                .telefono(request.telefono())
                .build();
        Inquilino saved = inquilinoRepository.save(inquilino);
        log.info("create_inquilino id={}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public InquilinoResponse update(Long id, InquilinoRequest request) {
        Inquilino inquilino = getInquilinoOrThrow(id);
        validateUniques(request, id);
        inquilino.setNombre(request.nombre());
        inquilino.setApellido(request.apellido());
        inquilino.setDni(request.dni());
        inquilino.setEmail(request.email());
        inquilino.setTelefono(request.telefono());
        log.info("update_inquilino id={}", id);
        return toResponse(inquilino);
    }

    @Transactional
    public void delete(Long id) {
        getInquilinoOrThrow(id);
        inquilinoRepository.deleteById(id);
        log.info("delete_inquilino id={}", id);
    }

    private void validateUniques(InquilinoRequest request, Long currentId) {
        inquilinoRepository.findByDni(request.dni())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new RecursoDuplicadoException("Ya existe un inquilino con el DNI " + request.dni());
                });
        inquilinoRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new RecursoDuplicadoException("Ya existe un inquilino con el email " + request.email());
                });
    }

    private Inquilino getInquilinoOrThrow(Long id) {
        return inquilinoRepository.findById(id)
                .orElseThrow(() -> new InquilinoNotFoundException(id));
    }

    private static InquilinoResponse toResponse(Inquilino inquilino) {
        return new InquilinoResponse(
                inquilino.getId(),
                inquilino.getNombre(),
                inquilino.getApellido(),
                inquilino.getDni(),
                inquilino.getEmail(),
                inquilino.getTelefono()
        );
    }
}
