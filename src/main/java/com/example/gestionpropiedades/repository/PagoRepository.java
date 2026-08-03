package com.example.gestionpropiedades.repository;

import com.example.gestionpropiedades.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByContratoIdAndPeriodo(Long contratoId, String periodo);

    boolean existsByContratoIdAndPeriodo(Long contratoId, String periodo);

    List<Pago> findByContratoId(Long contratoId);
}
