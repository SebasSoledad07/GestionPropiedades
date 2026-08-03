package com.example.gestionpropiedades.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Inquilino que arrienda una propiedad.
 */
@Entity
@Table(name = "inquilinos")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Inquilino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String telefono;
}
