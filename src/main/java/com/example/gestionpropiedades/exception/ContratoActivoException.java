package com.example.gestionpropiedades.exception;

/**
 * Lanzada cuando se intenta crear o activar un contrato para una propiedad
 * que ya tiene un contrato activo.
 */
public class ContratoActivoException extends RecursoDuplicadoException {

    public ContratoActivoException(Long propiedadId) {
        super("La propiedad con id " + propiedadId + " ya tiene un contrato activo");
    }
}
