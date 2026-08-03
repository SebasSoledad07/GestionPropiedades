package com.example.gestionpropiedades.entity;

import com.example.gestionpropiedades.entity.enums.EstadoPropiedad;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Propiedad en alquiler.
 */
@Entity
@Table(name = "propiedades")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Propiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(name = "precio_alquiler", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAlquiler;

    @Column(nullable = false)
    private Integer habitaciones;

    @Column(nullable = false)
    private Integer banos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPropiedad estado = EstadoPropiedad.DISPONIBLE;

    @Column(length = 500)
    private String descripcion;
}
