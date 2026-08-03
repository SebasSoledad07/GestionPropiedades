package com.example.gestionpropiedades.exception;

/**
 * Lanzada cuando se intenta registrar un pago para un período ya pagado del contrato.
 */
public class PagoDuplicadoException extends RecursoDuplicadoException {

    public PagoDuplicadoException(Long contratoId, String periodo) {
        super("El contrato con id " + contratoId + " ya tiene un pago registrado para el período " + periodo);
    }
}
