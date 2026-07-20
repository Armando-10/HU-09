package com.tutormatch.ms_usuarios.service;

import com.tutormatch.ms_usuarios.dto.RegistroDto;
import com.tutormatch.ms_usuarios.entity.Rol;
import com.tutormatch.ms_usuarios.entity.Usuario;
import com.tutormatch.ms_usuarios.repository.RolRepository;
import com.tutormatch.ms_usuarios.repository.UsuarioRepository;

import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.CompletableFuture;
import com.tutormatch.ms_usuarios.client.NotificacionClient;
import com.tutormatch.ms_usuarios.dto.NotificacionRequestDto;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificacionClient notificacionClient;

    // Registrar: se encarga de registrar un nuevo usuario en la base de datos.
    @Transactional
    public Usuario registrar(RegistroDto dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setEmail(dto.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        nuevoUsuario.setRoles(new java.util.HashSet<>());

        Rol rolAlumno = rolRepository.findByNombre("ROLE_ALUMNO")
                .orElseThrow(() -> new RuntimeException("Error crítico: El rol ALUMNO no existe en la base de datos"));

        nuevoUsuario.getRoles().add(rolAlumno);

        return usuarioRepository.save(nuevoUsuario);
    }

    // Obtener usuarios por ID
    public Usuario obtenerPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    // Solicitud para ser Tutor
    @Transactional
    public void solicitarRolTutor(UUID usuarioId, String justificacion) {
        if (justificacion == null || justificacion.trim().length() < 20) {
            throw new IllegalArgumentException("La justificación debe tener al menos 20 caracteres.");
        }

        Usuario usuario = obtenerPorId(usuarioId);

        if ("pendiente".equalsIgnoreCase(usuario.getEstadoSolicitud())) {
            throw new IllegalArgumentException("Ya tienes una solicitud en revisión.");
        }

        usuario.setJustificacion(justificacion.trim());
        usuario.setEstadoSolicitud("pendiente");
        usuarioRepository.save(usuario);
    }

    // Obtiene la lista de solicitudes pendientes
    @Transactional(readOnly = true)
    public List<com.tutormatch.ms_usuarios.dto.SolicitudPendienteDto> obtenerSolicitudesPendientes() {
        return usuarioRepository.findByEstadoSolicitud("pendiente").stream()
                .map(u -> new com.tutormatch.ms_usuarios.dto.SolicitudPendienteDto(
                        u.getId(), u.getNombre(), u.getEmail(), u.getJustificacion(), u.getEstadoSolicitud()))
                .collect(java.util.stream.Collectors.toList());
    }

    // Aprobación solicitud para ser tutor (Se agrega el rol)
    @Transactional
    public void aprobarSolicitud(UUID usuarioId) {
        Usuario usuario = obtenerPorId(usuarioId);
        Rol rolTutor = rolRepository.findByNombre("ROLE_TUTOR")
                .orElseThrow(() -> new RuntimeException("Error crítico: El rol TUTOR no existe en la BD"));

        usuario.getRoles().add(rolTutor);
        usuario.setEstadoSolicitud("aceptado");
        usuarioRepository.save(usuario);

        enviarNotificacionAsincrona(usuario, "APROBADA");
    }

    // Rechazar solicitud para ser tutor
    @Transactional
    public void rechazarSolicitud(UUID usuarioId) {
        Usuario usuario = obtenerPorId(usuarioId);
        usuario.setEstadoSolicitud("rechazado");
        usuarioRepository.save(usuario);

        enviarNotificacionAsincrona(usuario, "RECHAZADA");
    }

    // Método auxiliar para enviar correos
    private void enviarNotificacionAsincrona(Usuario usuario, String resolucion) {

        RequestAttributes context = RequestContextHolder.getRequestAttributes();

        CompletableFuture.runAsync(() -> {
            try {
                RequestContextHolder.setRequestAttributes(context);

                String titulo;
                String mensajeHtml;

                if ("APROBADA".equals(resolucion)) {
                    titulo = "¡Felicidades! Tu solicitud ha sido APROBADA";
                    mensajeHtml = String.format(
                            "<div style='font-family: sans-serif;'>" +
                                    "<h3 style='color: #10b981;'>¡Bienvenido al equipo de tutores, %s! 🎉</h3>" +
                                    "<p>Nos emociona informarte que tu solicitud para ser tutor ha sido evaluada y <strong>aprobada</strong>.</p>"
                                    +
                                    "<p>Inicia sesión en la plataforma y verás tu agenda habilitada para comenzar a compartir tu conocimiento.</p>"
                                    +
                                    "</div>",
                            usuario.getNombre());
                } else {
                    titulo = "Actualización sobre tu solicitud de Tutor";
                    mensajeHtml = String.format(
                            "<div style='font-family: sans-serif;'>" +
                                    "<h3 style='color: #ef4444;'>Hola %s,</h3>" +
                                    "<p>Hemos revisado cuidadosamente tu solicitud para convertirte en tutor. Lamentablemente, en esta ocasión <strong>no ha sido aprobada</strong>.</p>"
                                    +
                                    "<p>Esto puede deberse a la validación de tus requisitos actuales. Te invitamos a seguir participando como alumno y a volver a intentarlo en el futuro.</p>"
                                    +
                                    "</div>",
                            usuario.getNombre());
                }

                NotificacionRequestDto noti = new NotificacionRequestDto();
                noti.setUsuarioId(usuario.getId());
                noti.setCorreoDestino(usuario.getEmail());
                noti.setTitulo(titulo);
                noti.setMensaje(mensajeHtml);

                notificacionClient.enviarNotificacion(noti);
            } catch (Exception e) {
                System.err.println("Error asíncrono enviando correo de evaluación: " + e.getMessage());
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        });
    }
}