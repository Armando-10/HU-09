package com.tutormatch.ms_usuarios.repository;

import com.tutormatch.ms_usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByEstadoSolicitud(String estadoSolicitud);
}