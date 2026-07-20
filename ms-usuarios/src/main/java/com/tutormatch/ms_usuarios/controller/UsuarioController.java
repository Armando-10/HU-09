package com.tutormatch.ms_usuarios.controller;

import com.tutormatch.ms_usuarios.dto.RegistroDto;
import com.tutormatch.ms_usuarios.entity.Usuario;
import com.tutormatch.ms_usuarios.service.UsuarioService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // Endpoint para registrar un nuevo usuario
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroDto dto) {
        try {
            service.registrar(dto);
            return ResponseEntity.ok("Usuario registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para obtener la información de un usuario por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> obtenerUsuario(@PathVariable UUID id) {
        Usuario usuario = service.obtenerPorId(id);
        Map<String, String> response = new HashMap<>();
        response.put("email", usuario.getEmail());
        response.put("nombre", usuario.getNombre());
        response.put("estadoSolicitud",
                usuario.getEstadoSolicitud() != null ? usuario.getEstadoSolicitud() : "NINGUNA");

        return ResponseEntity.ok(response);
    }

    // Enviar solicitud para ser tutor (Solo Alumnos)
    @PostMapping("/peticiones")
    @PreAuthorize("hasRole('ROLE_ALUMNO')")
    public ResponseEntity<String> enviarSolicitudTutor(
            @RequestBody com.tutormatch.ms_usuarios.dto.SolicitudTutorRequestDto dto,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        try {
            String userIdStr = jwt.getClaimAsString("usuario_id");
            if (userIdStr == null)
                userIdStr = jwt.getSubject();
            UUID usuarioId = UUID.fromString(userIdStr);

            service.solicitarRolTutor(usuarioId, dto.getJustificacion());
            return ResponseEntity.ok("Solicitud enviada correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Ver solicitudes para ser tutor pendientes (Solo Admin)
    @GetMapping("/peticiones/pendientes")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<java.util.List<com.tutormatch.ms_usuarios.dto.SolicitudPendienteDto>> obtenerPendientes() {
        return ResponseEntity.ok(service.obtenerSolicitudesPendientes());
    }

    // Aprobar solicitudes para ser tutor (Solo Admin)
    @PutMapping("/peticiones/{id}/aprobar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> aprobarSolicitud(@PathVariable UUID id) {
        service.aprobarSolicitud(id);
        return ResponseEntity.ok("Solicitud aprobada exitosamente.");
    }

    // Rechazar solicitud para ser tutor (Solo Admin)
    @PutMapping("/peticiones/{id}/rechazar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> rechazarSolicitud(@PathVariable UUID id) {
        service.rechazarSolicitud(id);
        return ResponseEntity.ok("Solicitud rechazada.");
    }
}