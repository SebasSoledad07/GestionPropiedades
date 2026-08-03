package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.ContratoRequest;
import com.example.gestionpropiedades.dto.ContratoResponse;
import com.example.gestionpropiedades.entity.Contrato;
import com.example.gestionpropiedades.entity.Inquilino;
import com.example.gestionpropiedades.entity.Propiedad;
import com.example.gestionpropiedades.entity.enums.EstadoContrato;
import com.example.gestionpropiedades.entity.enums.EstadoPropiedad;
import com.example.gestionpropiedades.exception.ContratoActivoException;
import com.example.gestionpropiedades.exception.ContratoNotFoundException;
import com.example.gestionpropiedades.exception.InquilinoNotFoundException;
import com.example.gestionpropiedades.exception.PropiedadNotFoundException;
import com.example.gestionpropiedades.repository.ContratoRepository;
import com.example.gestionpropiedades.repository.InquilinoRepository;
import com.example.gestionpropiedades.repository.PropiedadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de negocio para contratos.
 * <p>
 * Aplica la regla de negocio 1 (una propiedad no puede tener varios contratos activos)
 * y la regla 3 (el estado de la propiedad se actualiza según el contrato).
 */
@Service
public class ContratoService {

    private static final Logger log = LoggerFactory.getLogger(ContratoService.class);

    private final ContratoRepository contratoRepository;
    private final PropiedadRepository propiedadRepository;
    private final InquilinoRepository inquilinoRepository;

    public ContratoService(ContratoRepository contratoRepository,
                           PropiedadRepository propiedadRepository,
                           InquilinoRepository inquilinoRepository) {
        this.contratoRepository = contratoRepository;
        this.propiedadRepository = propiedadRepository;
        this.inquilinoRepository = inquilinoRepository;
    }

    @Transactional(readOnly = true)
    public List<ContratoResponse> findAll() {
        return contratoRepository.findAll().stream()
                .map(ContratoService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContratoResponse findById(Long id) {
        return toResponse(getContratoOrThrow(id));
    }

    @Transactional
    public ContratoResponse create(ContratoRequest request) {
        Propiedad propiedad = getPropiedadOrThrow(request.propiedadId());
        Inquilino inquilino = getInquilinoOrThrow(request.inquilinoId());
        validateFechas(request.fechaInicio(), request.fechaFin());
        validateNoContratoActivo(request.propiedadId(), null);

        Contrato contrato = Contrato.builder()
                .propiedad(propiedad)
                .inquilino(inquilino)
                .fechaInicio(request.fechaInicio())
                .fechaFin(request.fechaFin())
                .rentaMensual(request.rentaMensual())
                .build();
        Contrato saved = contratoRepository.save(contrato);
        propiedad.setEstado(EstadoPropiedad.OCUPADA);
        log.info("create_contrato id={} propiedad_id={}", saved.getId(), request.propiedadId());
        return toResponse(saved);
    }

    @Transactional
    public ContratoResponse update(Long id, ContratoRequest request) {
        Contrato contrato = getContratoOrThrow(id);
        Propiedad nuevaPropiedad = getPropiedadOrThrow(request.propiedadId());
        Inquilino inquilino = getInquilinoOrThrow(request.inquilinoId());
        validateFechas(request.fechaInicio(), request.fechaFin());

        if (contrato.getEstado() == EstadoContrato.ACTIVO) {
            validateNoContratoActivo(request.propiedadId(), id);
        }

        if (contrato.getEstado() == EstadoContrato.ACTIVO && !contrato.getPropiedad().getId().equals(request.propiedadId())) {
            liberarPropiedad(contrato.getPropiedad(), id);
            nuevaPropiedad.setEstado(EstadoPropiedad.OCUPADA);
        }

        contrato.setPropiedad(nuevaPropiedad);
        contrato.setInquilino(inquilino);
        contrato.setFechaInicio(request.fechaInicio());
        contrato.setFechaFin(request.fechaFin());
        contrato.setRentaMensual(request.rentaMensual());
        log.info("update_contrato id={}", id);
        return toResponse(contrato);
    }

    @Transactional
    public ContratoResponse activar(Long id) {
        Contrato contrato = getContratoOrThrow(id);
        Long propiedadId = contrato.getPropiedad().getId();
        if (contrato.getEstado() != EstadoContrato.ACTIVO) {
            validateNoContratoActivo(propiedadId, id);
            contrato.setEstado(EstadoContrato.ACTIVO);
            contrato.getPropiedad().setEstado(EstadoPropiedad.OCUPADA);
            log.info("activar_contrato id={}", id);
        }
        return toResponse(contrato);
    }

    @Transactional
    public ContratoResponse finalizar(Long id) {
        Contrato contrato = getContratoOrThrow(id);
        if (contrato.getEstado() == EstadoContrato.ACTIVO) {
            contrato.setEstado(EstadoContrato.FINALIZADO);
            liberarPropiedad(contrato.getPropiedad(), id);
            log.info("finalizar_contrato id={}", id);
        }
        return toResponse(contrato);
    }

    @Transactional
    public ContratoResponse cancelar(Long id) {
        Contrato contrato = getContratoOrThrow(id);
        if (contrato.getEstado() == EstadoContrato.ACTIVO) {
            contrato.setEstado(EstadoContrato.CANCELADO);
            liberarPropiedad(contrato.getPropiedad(), id);
            log.info("cancelar_contrato id={}", id);
        }
        return toResponse(contrato);
    }

    @Transactional
    public void delete(Long id) {
        Contrato contrato = getContratoOrThrow(id);
        if (contrato.getEstado() == EstadoContrato.ACTIVO) {
            liberarPropiedad(contrato.getPropiedad(), id);
        }
        contratoRepository.deleteById(id);
        log.info("delete_contrato id={}", id);
    }

    private void validateNoContratoActivo(Long propiedadId, Long contratoIdExcluido) {
        boolean tieneActivo = contratoIdExcluido == null
                ? contratoRepository.existsByPropiedadIdAndEstado(propiedadId, EstadoContrato.ACTIVO)
                : contratoRepository.existsByPropiedadIdAndEstadoAndIdNot(propiedadId, EstadoContrato.ACTIVO, contratoIdExcluido);
        if (tieneActivo) {
            throw new ContratoActivoException(propiedadId);
        }
    }

    private void liberarPropiedad(Propiedad propiedad, Long contratoIdExcluido) {
        if (!contratoRepository.existsByPropiedadIdAndEstadoAndIdNot(
                propiedad.getId(), EstadoContrato.ACTIVO, contratoIdExcluido)) {
            propiedad.setEstado(EstadoPropiedad.DISPONIBLE);
        }
    }

    private void validateFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private Contrato getContratoOrThrow(Long id) {
        return contratoRepository.findById(id)
                .orElseThrow(() -> new ContratoNotFoundException(id));
    }

    private Propiedad getPropiedadOrThrow(Long id) {
        return propiedadRepository.findById(id)
                .orElseThrow(() -> new PropiedadNotFoundException(id));
    }

    private Inquilino getInquilinoOrThrow(Long id) {
        return inquilinoRepository.findById(id)
                .orElseThrow(() -> new InquilinoNotFoundException(id));
    }

    private static ContratoResponse toResponse(Contrato contrato) {
        return new ContratoResponse(
                contrato.getId(),
                contrato.getPropiedad().getId(),
                contrato.getInquilino().getId(),
                contrato.getFechaInicio(),
                contrato.getFechaFin(),
                contrato.getRentaMensual(),
                contrato.getEstado()
        );
    }
}
