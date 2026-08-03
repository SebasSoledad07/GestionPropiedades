package com.example.gestionpropiedades.exception;

/**
 * Lanzada cuando no se encuentra un pago.
 */
public class PagoNotFoundException extends ResourceNotFoundException {

    public PagoNotFoundException(Long id) {
        super("Pago con id " + id + " no encontrado");
    }
}
