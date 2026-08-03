package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.PagoRequest;
import com.example.gestionpropiedades.dto.PagoResponse;
import com.example.gestionpropiedades.entity.Contrato;
import com.example.gestionpropiedades.entity.Pago;
import com.example.gestionpropiedades.exception.ContratoNotFoundException;
import com.example.gestionpropiedades.exception.PagoDuplicadoException;
import com.example.gestionpropiedades.exception.PagoNotFoundException;
import com.example.gestionpropiedades.repository.ContratoRepository;
import com.example.gestionpropiedades.repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para pagos.
 */
@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository pagoRepository;
    private final ContratoRepository contratoRepository;

    public PagoService(PagoRepository pagoRepository, ContratoRepository contratoRepository) {
        this.pagoRepository = pagoRepository;
        this.contratoRepository = contratoRepository;
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> findAll() {
        return pagoRepository.findAll().stream()
                .map(PagoService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PagoResponse findById(Long id) {
        return toResponse(getPagoOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> findByContratoId(Long contratoId) {
        return pagoRepository.findByContratoId(contratoId).stream()
                .map(PagoService::toResponse)
                .toList();
    }

    @Transactional
    public PagoResponse create(PagoRequest request) {
        Contrato contrato = contratoRepository.findById(request.contratoId())
                .orElseThrow(() -> new ContratoNotFoundException(request.contratoId()));
        validatePeriodoUnico(request.contratoId(), request.periodo(), null);

        Pago pago = Pago.builder()
                .contrato(contrato)
                .monto(request.monto())
                .fechaPago(request.fechaPago())
                .periodo(request.periodo())
                .metodoPago(request.metodoPago())
                .build();
        Pago saved = pagoRepository.save(pago);
        log.info("create_pago id={} contrato_id={}", saved.getId(), request.contratoId());
        return toResponse(saved);
    }

    @Transactional
    public PagoResponse update(Long id, PagoRequest request) {
        Pago pago = getPagoOrThrow(id);
        Contrato contrato = contratoRepository.findById(request.contratoId())
                .orElseThrow(() -> new ContratoNotFoundException(request.contratoId()));
        validatePeriodoUnico(request.contratoId(), request.periodo(), id);

        pago.setContrato(contrato);
        pago.setMonto(request.monto());
        pago.setFechaPago(request.fechaPago());
        pago.setPeriodo(request.periodo());
        pago.setMetodoPago(request.metodoPago());
        log.info("update_pago id={}", id);
        return toResponse(pago);
    }

    @Transactional
    public void delete(Long id) {
        getPagoOrThrow(id);
        pagoRepository.deleteById(id);
        log.info("delete_pago id={}", id);
    }

    private Pago getPagoOrThrow(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new PagoNotFoundException(id));
    }

    private void validatePeriodoUnico(Long contratoId, String periodo, Long pagoIdExcluido) {
        boolean duplicado = pagoIdExcluido == null
                ? pagoRepository.existsByContratoIdAndPeriodo(contratoId, periodo)
                : pagoRepository.existsByContratoIdAndPeriodoAndIdNot(contratoId, periodo, pagoIdExcluido);
        if (duplicado) {
            throw new PagoDuplicadoException(contratoId, periodo);
        }
    }

    private static PagoResponse toResponse(Pago pago) {
        return new PagoResponse(
                pago.getId(),
                pago.getContrato().getId(),
                pago.getMonto(),
                pago.getFechaPago(),
                pago.getPeriodo(),
                pago.getMetodoPago(),
                pago.getEstado()
        );
    }
}
