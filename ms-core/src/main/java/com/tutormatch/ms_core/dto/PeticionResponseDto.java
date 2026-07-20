package com.tutormatch.ms_core.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de salida para HU-34: Listar peticiones en el tablero.
 */
public class PeticionResponseDto {

    private UUID id;
    private UUID alumnoId;
    private String alumnoNombre;
    private String materia;
    private String descripcion;
    private String estado;
    private UUID tutorAtendioId;
    private String tutorAtendioNombre;
    private LocalDateTime creadoEn;

    public PeticionResponseDto() {}

    public PeticionResponseDto(UUID id, UUID alumnoId, String alumnoNombre, String materia,
                               String descripcion, String estado, UUID tutorAtendioId,
                               String tutorAtendioNombre, LocalDateTime creadoEn) {
        this.id = id;
        this.alumnoId = alumnoId;
        this.alumnoNombre = alumnoNombre;
        this.materia = materia;
        this.descripcion = descripcion;
        this.estado = estado;
        this.tutorAtendioId = tutorAtendioId;
        this.tutorAtendioNombre = tutorAtendioNombre;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAlumnoId() { return alumnoId; }
    public void setAlumnoId(UUID alumnoId) { this.alumnoId = alumnoId; }

    public String getAlumnoNombre() { return alumnoNombre; }
    public void setAlumnoNombre(String alumnoNombre) { this.alumnoNombre = alumnoNombre; }

    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public UUID getTutorAtendioId() { return tutorAtendioId; }
    public void setTutorAtendioId(UUID tutorAtendioId) { this.tutorAtendioId = tutorAtendioId; }

    public String getTutorAtendioNombre() { return tutorAtendioNombre; }
    public void setTutorAtendioNombre(String tutorAtendioNombre) { this.tutorAtendioNombre = tutorAtendioNombre; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
