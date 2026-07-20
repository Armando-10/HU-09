package com.tutormatch.ms_core.repository;

import com.tutormatch.ms_core.entity.Peticion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PeticionRepository extends JpaRepository<Peticion, UUID> {

    /**
     * HU-34: Listar todas las peticiones ordenadas de más reciente a más antigua.
     */
    List<Peticion> findAllByOrderByCreadoEnDesc();

    /**
     * HU-35: Buscar petición por ID y alumnoId para validar que pertenece al usuario.
     */
    Optional<Peticion> findByIdAndAlumnoId(UUID id, UUID alumnoId);
}
