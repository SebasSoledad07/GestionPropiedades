package com.example.gestionpropiedades.repository;

import com.example.gestionpropiedades.entity.Pago;
import com.example.gestionpropiedades.entity.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByContratoIdAndPeriodo(Long contratoId, String periodo);

    boolean existsByContratoIdAndPeriodo(Long contratoId, String periodo);

    boolean existsByContratoIdAndPeriodoAndIdNot(Long contratoId, String periodo, Long id);

    List<Pago> findByContratoId(Long contratoId);

    List<Pago> findByEstado(EstadoPago estado);

    long countByEstado(EstadoPago estado);

    @Query("select coalesce(sum(p.monto), 0) from Pago p where p.estado = :estado")
    BigDecimal sumMontoByEstado(@Param("estado") EstadoPago estado);
}
