package com.raphaelprojetos.Sentinel.controller;

import com.raphaelprojetos.Sentinel.entitys.Alerta;
import com.raphaelprojetos.Sentinel.services.SentinelService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/alertas")
public class SentinelController {

    private final SentinelService sentinelService;

    public  SentinelController (SentinelService sentinelService){

        this.sentinelService = sentinelService;

    }

    @PostMapping
    @ResponseBody
    public Alerta create (Alerta alerta){

        return sentinelService.create(alerta);

    }

    @GetMapping
    @ResponseBody
    public List<Alerta> list(){

        return sentinelService.list();

    }

    @PutMapping
    @ResponseBody
    public Alerta update (@RequestBody Alerta alerta){

        return sentinelService.update(alerta);

    }

    @DeleteMapping("{ID}")
    @ResponseBody
    public List<Alerta> delete(@PathVariable ("ID") long ID){

        sentinelService.delete(ID);
        return list();
    }


}
