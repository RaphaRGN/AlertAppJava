package com.raphaelprojetos.Sentinel.services;

import com.raphaelprojetos.Sentinel.entitys.Alerta;
import com.raphaelprojetos.Sentinel.repository.SentinelRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SentinelService {

    private final SentinelRepository sentinelRepository;

    public SentinelService (SentinelRepository repository){

      this.sentinelRepository = repository;

}

    public Alerta create(Alerta alerta){

       return sentinelRepository.save(alerta);

    }

    public List<Alerta> list() {

        Sort sort = Sort.by("ID").descending().and(Sort.by("titulo").ascending());
        return sentinelRepository.findAll(sort);

    }

    public Alerta update (Alerta alerta){

       return sentinelRepository.save(alerta);

    }

    public List<Alerta> delete (Long ID){

        sentinelRepository.deleteById(ID);
        return list();

    }



}
