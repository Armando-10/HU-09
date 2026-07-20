package com.tutormatch.ms_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SolicitudPendienteDto {
    private UUID id;
    private String nombre;
    private String email;
    private String justificacion;
    private String estado;
}