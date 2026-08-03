package com.example.gestionpropiedades.exception;

/**
 * Lanzada cuando no se encuentra un usuario.
 */
public class UsuarioNotFoundException extends ResourceNotFoundException {

    public UsuarioNotFoundException(Long id) {
        super("Usuario con id " + id + " no encontrado");
    }
}
