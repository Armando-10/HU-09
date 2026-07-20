package com.tutormatch.ms_core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity que mapea schema_core.peticiones.
 * HU-33 a HU-36: sistema de peticiones de asesoría.
 */
@Entity
@Table(name = "peticiones", schema = "schema_core")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Peticion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** ID del alumno que creó la petición */
    @Column(name = "alumno_id", nullable = false)
    private UUID alumnoId;

    /** Nombre del alumno (desnormalizado para evitar llamadas entre microservicios) */
    @Column(name = "alumno_nombre", nullable = false)
    private String alumnoNombre;

    /** Nombre de la materia solicitada */
    @Column(name = "materia", nullable = false)
    private String materia;

    /** Descripción del tema o duda específica */
    @Column(name = "descripcion", nullable = false, columnDefinition = "text")
    private String descripcion;

    /** Estado: "activa" | "atendida" */
    @Column(name = "estado", nullable = false)
    private String estado;

    /** ID del tutor que marcó la petición como atendida (nullable) */
    @Column(name = "tutor_atendio_id")
    private UUID tutorAtendioId;

    /** Nombre del tutor que la atendió (desnormalizado) */
    @Column(name = "tutor_atendio_nombre")
    private String tutorAtendioNombre;

    /** Fecha de creación */
    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;
}
