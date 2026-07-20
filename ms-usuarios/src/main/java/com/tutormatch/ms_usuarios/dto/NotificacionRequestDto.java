package com.tutormatch.ms_usuarios.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class NotificacionRequestDto {
    private UUID usuarioId;
    private String correoDestino;
    private String titulo;
    private String mensaje;
}