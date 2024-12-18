package com.raphaelprojetos.sentinel.services;

import com.raphaelprojetos.sentinel.entities.Alerta;
import com.raphaelprojetos.sentinel.repository.AlertaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository repository){

      this.alertaRepository = repository;

}

    public Alerta create(Alerta alerta){

       return alertaRepository.save(alerta);

    }

    public List<Alerta> list() {

        Sort sort = Sort.by("Id").descending().and(Sort.by("titulo").ascending());
        return alertaRepository.findAll(sort);

    }

    public Alerta update (Alerta alerta){

       return alertaRepository.save(alerta);

    }

    public List<Alerta> delete (Long Id){

        alertaRepository.deleteById(Id);
        return list();

    }



}
