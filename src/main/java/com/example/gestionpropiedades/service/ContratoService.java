package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.ContratoRequest;
import com.example.gestionpropiedades.dto.ContratoResponse;
import com.example.gestionpropiedades.entity.Contrato;
import com.example.gestionpropiedades.entity.Inquilino;
import com.example.gestionpropiedades.entity.Propiedad;
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

        Contrato contrato = Contrato.builder()
                .propiedad(propiedad)
                .inquilino(inquilino)
                .fechaInicio(request.fechaInicio())
                .fechaFin(request.fechaFin())
                .rentaMensual(request.rentaMensual())
                .build();
        Contrato saved = contratoRepository.save(contrato);
        log.info("create_contrato id={} propiedad_id={}", saved.getId(), request.propiedadId());
        return toResponse(saved);
    }

    @Transactional
    public ContratoResponse update(Long id, ContratoRequest request) {
        Contrato contrato = getContratoOrThrow(id);
        Propiedad propiedad = getPropiedadOrThrow(request.propiedadId());
        Inquilino inquilino = getInquilinoOrThrow(request.inquilinoId());
        validateFechas(request.fechaInicio(), request.fechaFin());

        contrato.setPropiedad(propiedad);
        contrato.setInquilino(inquilino);
        contrato.setFechaInicio(request.fechaInicio());
        contrato.setFechaFin(request.fechaFin());
        contrato.setRentaMensual(request.rentaMensual());
        log.info("update_contrato id={}", id);
        return toResponse(contrato);
    }

    @Transactional
    public void delete(Long id) {
        getContratoOrThrow(id);
        contratoRepository.deleteById(id);
        log.info("delete_contrato id={}", id);
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
