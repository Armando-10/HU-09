package com.tutormatch.ms_usuarios.dto;

import java.util.UUID;

public class SolicitudPendienteDto {
    private UUID id;
    private String nombre;
    private String email;
    private String justificacion;
    private String estado;

    public SolicitudPendienteDto() {}

    public SolicitudPendienteDto(UUID id, String nombre, String email, String justificacion, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.justificacion = justificacion;
        this.estado = estado;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}