package com.tutormatch.ms_core.dto;

/**
 * DTO de entrada para HU-33: Crear una petición de asesoría.
 */
public class PeticionRequestDto {

    /** Nombre de la materia (mínimo 3 caracteres) */
    private String materia;

    /** Descripción del tema específico (mínimo 10 caracteres) */
    private String descripcion;

    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
