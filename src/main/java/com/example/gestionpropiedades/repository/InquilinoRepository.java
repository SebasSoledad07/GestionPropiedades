package com.example.gestionpropiedades.repository;

import com.example.gestionpropiedades.entity.Inquilino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InquilinoRepository extends JpaRepository<Inquilino, Long> {

    Optional<Inquilino> findByDni(String dni);

    Optional<Inquilino> findByEmail(String email);
}
