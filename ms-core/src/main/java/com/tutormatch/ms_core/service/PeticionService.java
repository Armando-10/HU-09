package com.tutormatch.ms_core.service;

import com.tutormatch.ms_core.dto.PeticionRequestDto;
import com.tutormatch.ms_core.dto.PeticionResponseDto;
import com.tutormatch.ms_core.entity.Peticion;
import com.tutormatch.ms_core.repository.PeticionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Lógica de negocio para EP-09: Tablero de Peticiones.
 * HU-33: Crear | HU-34: Listar | HU-35: Eliminar (solo dueño) | HU-36: Marcar atendida (tutor)
 */
@Service
public class PeticionService {

    private final PeticionRepository peticionRepository;

    public PeticionService(PeticionRepository peticionRepository) {
        this.peticionRepository = peticionRepository;
    }

    // -----------------------------------------------------------------------
    // HU-33: Crear petición
    // -----------------------------------------------------------------------
    @Transactional
    public PeticionResponseDto crear(PeticionRequestDto dto, UUID alumnoId, String alumnoNombre) {
        if (dto.getMateria() == null || dto.getMateria().trim().length() < 3) {
            throw new IllegalArgumentException("La materia debe tener al menos 3 caracteres.");
        }
        if (dto.getDescripcion() == null || dto.getDescripcion().trim().length() < 10) {
            throw new IllegalArgumentException("La descripción debe tener al menos 10 caracteres.");
        }

        Peticion peticion = new Peticion();
        peticion.setAlumnoId(alumnoId);
        peticion.setAlumnoNombre(alumnoNombre);
        peticion.setMateria(dto.getMateria().trim());
        peticion.setDescripcion(dto.getDescripcion().trim());
        peticion.setEstado("activa");
        peticion.setCreadoEn(LocalDateTime.now());

        return mapToDto(peticionRepository.save(peticion));
    }

    // -----------------------------------------------------------------------
    // HU-34: Listar todas las peticiones (más reciente primero)
    // -----------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<PeticionResponseDto> listar() {
        return peticionRepository.findAllByOrderByCreadoEnDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------------------
    // HU-35: Eliminar petición (solo el alumno dueño puede eliminar)
    // -----------------------------------------------------------------------
    @Transactional
    public void eliminar(UUID peticionId, UUID alumnoId) {
        Peticion peticion = peticionRepository.findByIdAndAlumnoId(peticionId, alumnoId)
                .orElseThrow(() -> new SecurityException("No tienes permiso para eliminar esta petición."));

        peticionRepository.delete(peticion);
    }

    // -----------------------------------------------------------------------
    // HU-36: Marcar petición como atendida (solo TUTOR)
    // -----------------------------------------------------------------------
    @Transactional
    public PeticionResponseDto marcarAtendida(UUID peticionId, UUID tutorId, String tutorNombre) {
        Peticion peticion = peticionRepository.findById(peticionId)
                .orElseThrow(() -> new IllegalArgumentException("Petición no encontrada."));

        if ("atendida".equals(peticion.getEstado())) {
            throw new IllegalArgumentException("Esta petición ya fue marcada como atendida.");
        }

        peticion.setEstado("atendida");
        peticion.setTutorAtendioId(tutorId);
        peticion.setTutorAtendioNombre(tutorNombre);

        return mapToDto(peticionRepository.save(peticion));
    }

    // -----------------------------------------------------------------------
    // Helper: Entity → DTO
    // -----------------------------------------------------------------------
    private PeticionResponseDto mapToDto(Peticion p) {
        return new PeticionResponseDto(
                p.getId(),
                p.getAlumnoId(),
                p.getAlumnoNombre(),
                p.getMateria(),
                p.getDescripcion(),
                p.getEstado(),
                p.getTutorAtendioId(),
                p.getTutorAtendioNombre(),
                p.getCreadoEn()
        );
    }
}
