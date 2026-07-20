package com.tutormatch.ms_core.controller;

import com.tutormatch.ms_core.dto.PeticionRequestDto;
import com.tutormatch.ms_core.dto.PeticionResponseDto;
import com.tutormatch.ms_core.service.PeticionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para EP-09: Tablero de Peticiones de Asesoría.
 *
 * POST   /api/core/peticiones               HU-33 – ROLE_ALUMNO
 * GET    /api/core/peticiones               HU-34 – ROLE_ALUMNO | ROLE_TUTOR
 * DELETE /api/core/peticiones/{id}          HU-35 – ROLE_ALUMNO (solo dueño)
 * PATCH  /api/core/peticiones/{id}/atender  HU-36 – ROLE_TUTOR
 */
@RestController
@RequestMapping("/api/core/peticiones")
public class PeticionController {

    private final PeticionService peticionService;

    public PeticionController(PeticionService peticionService) {
        this.peticionService = peticionService;
    }

    // -----------------------------------------------------------------------
    // HU-33: POST — Crear petición
    // -----------------------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ALUMNO')")
    public ResponseEntity<PeticionResponseDto> crear(
            @RequestBody PeticionRequestDto dto,
            @AuthenticationPrincipal Jwt jwt) {

        UUID alumnoId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        String alumnoNombre = jwt.getClaimAsString("nombre");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(peticionService.crear(dto, alumnoId, alumnoNombre));
    }

    // -----------------------------------------------------------------------
    // HU-34: GET — Listar todas las peticiones
    // -----------------------------------------------------------------------
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ALUMNO') or hasRole('ROLE_TUTOR')")
    public ResponseEntity<List<PeticionResponseDto>> listar() {
        return ResponseEntity.ok(peticionService.listar());
    }

    // -----------------------------------------------------------------------
    // HU-35: DELETE — Eliminar petición propia (ALUMNO)
    // -----------------------------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ALUMNO')")
    public ResponseEntity<Void> eliminar(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        UUID alumnoId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        peticionService.eliminar(id, alumnoId);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------------------------
    // HU-36: PATCH — Marcar petición como atendida (TUTOR)
    // -----------------------------------------------------------------------
    @PatchMapping("/{id}/atender")
    @PreAuthorize("hasRole('ROLE_TUTOR')")
    public ResponseEntity<PeticionResponseDto> marcarAtendida(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        UUID tutorId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        String tutorNombre = jwt.getClaimAsString("nombre");
        return ResponseEntity.ok(peticionService.marcarAtendida(id, tutorId, tutorNombre));
    }

    // -----------------------------------------------------------------------
    // Manejo de errores
    // -----------------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidation(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurity(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}
