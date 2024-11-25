package com.raphaelprojetos.Sentinel.repository;

import com.raphaelprojetos.Sentinel.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {
}
