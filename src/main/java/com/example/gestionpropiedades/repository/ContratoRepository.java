package com.example.gestionpropiedades.repository;

import com.example.gestionpropiedades.entity.Contrato;
import com.example.gestionpropiedades.entity.enums.EstadoContrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByEstado(EstadoContrato estado);

    boolean existsByPropiedadIdAndEstado(Long propiedadId, EstadoContrato estado);
}
