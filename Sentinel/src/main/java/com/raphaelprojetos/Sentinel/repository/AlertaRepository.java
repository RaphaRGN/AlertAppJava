package com.raphaelprojetos.Sentinel.repository;

import com.raphaelprojetos.Sentinel.entities.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaRepository extends JpaRepository <Alerta, Long> {
}
