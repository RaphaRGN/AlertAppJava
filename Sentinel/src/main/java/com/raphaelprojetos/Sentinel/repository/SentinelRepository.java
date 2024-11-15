package com.raphaelprojetos.Sentinel.repository;

import com.raphaelprojetos.Sentinel.entitys.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentinelRepository extends JpaRepository <Alerta, Long> {
}
