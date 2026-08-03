package com.example.gestionpropiedades.repository;

import com.example.gestionpropiedades.entity.Propiedad;
import com.example.gestionpropiedades.entity.enums.EstadoPropiedad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {

    List<Propiedad> findByEstado(EstadoPropiedad estado);
}
