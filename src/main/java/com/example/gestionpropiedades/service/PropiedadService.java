package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.PropiedadRequest;
import com.example.gestionpropiedades.dto.PropiedadResponse;
import com.example.gestionpropiedades.entity.Propiedad;
import com.example.gestionpropiedades.exception.PropiedadNotFoundException;
import com.example.gestionpropiedades.repository.PropiedadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para propiedades.
 */
@Service
public class PropiedadService {

    private static final Logger log = LoggerFactory.getLogger(PropiedadService.class);

    private final PropiedadRepository propiedadRepository;

    public PropiedadService(PropiedadRepository propiedadRepository) {
        this.propiedadRepository = propiedadRepository;
    }

    @Transactional(readOnly = true)
    public List<PropiedadResponse> findAll() {
        return propiedadRepository.findAll().stream()
                .map(PropiedadService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PropiedadResponse findById(Long id) {
        return toResponse(getPropiedadOrThrow(id));
    }

    @Transactional
    public PropiedadResponse create(PropiedadRequest request) {
        Propiedad propiedad = Propiedad.builder()
                .direccion(request.direccion())
                .ciudad(request.ciudad())
                .precioAlquiler(request.precioAlquiler())
                .habitaciones(request.habitaciones())
                .banos(request.banos())
                .descripcion(request.descripcion())
                .build();
        Propiedad saved = propiedadRepository.save(propiedad);
        log.info("create_propiedad id={}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public PropiedadResponse update(Long id, PropiedadRequest request) {
        Propiedad propiedad = getPropiedadOrThrow(id);
        propiedad.setDireccion(request.direccion());
        propiedad.setCiudad(request.ciudad());
        propiedad.setPrecioAlquiler(request.precioAlquiler());
        propiedad.setHabitaciones(request.habitaciones());
        propiedad.setBanos(request.banos());
        propiedad.setDescripcion(request.descripcion());
        log.info("update_propiedad id={}", id);
        return toResponse(propiedad);
    }

    @Transactional
    public void delete(Long id) {
        getPropiedadOrThrow(id);
        propiedadRepository.deleteById(id);
        log.info("delete_propiedad id={}", id);
    }

    private Propiedad getPropiedadOrThrow(Long id) {
        return propiedadRepository.findById(id)
                .orElseThrow(() -> new PropiedadNotFoundException(id));
    }

    private static PropiedadResponse toResponse(Propiedad propiedad) {
        return new PropiedadResponse(
                propiedad.getId(),
                propiedad.getDireccion(),
                propiedad.getCiudad(),
                propiedad.getPrecioAlquiler(),
                propiedad.getHabitaciones(),
                propiedad.getBanos(),
                propiedad.getEstado(),
                propiedad.getDescripcion()
        );
    }
}
