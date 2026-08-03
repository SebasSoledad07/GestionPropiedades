package com.example.gestionpropiedades.entity;

import com.example.gestionpropiedades.entity.enums.EstadoPago;
import com.example.gestionpropiedades.entity.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pago mensual asociado a un contrato. Un período solo puede pagarse una vez por contrato.
 */
@Entity
@Table(
        name = "pagos",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pago_contrato_periodo",
                columnNames = {"contrato_id", "periodo"}
        )
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contrato_id", nullable = false)
    private Contrato contrato;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @Column(nullable = false, length = 7)
    private String periodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Version
    private Long version;
}
