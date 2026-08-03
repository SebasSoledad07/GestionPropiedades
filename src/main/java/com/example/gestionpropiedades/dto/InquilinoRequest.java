package com.example.gestionpropiedades.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos de entrada para crear o actualizar un inquilino.
 */
public record InquilinoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
        String apellido,

        @NotBlank(message = "El DNI es obligatorio")
        @Size(max = 20, message = "El DNI no puede exceder 20 caracteres")
        String dni,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 150, message = "El email no puede exceder 150 caracteres")
        String email,

        @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
        String telefono
) {
}
