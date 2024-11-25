package com.raphaelprojetos.Sentinel.controller;

import com.raphaelprojetos.Sentinel.entities.Alerta;
import com.raphaelprojetos.Sentinel.services.AlertaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/alertas")
public class SentinelController {

    private final AlertaService alertaService;

    public  SentinelController (AlertaService alertaService){

        this.alertaService = alertaService;

    }

    @PostMapping
    @ResponseBody
    public Alerta create (Alerta alerta){

        return alertaService.create(alerta);

    }

    @GetMapping
    @ResponseBody
    public List<Alerta> list(){

        return alertaService.list();

    }

    @PutMapping
    @ResponseBody
    public Alerta update (@RequestBody Alerta alerta){

        return alertaService.update(alerta);

    }

    @DeleteMapping("{ID}")
    @ResponseBody
    public List<Alerta> delete(@PathVariable ("ID") long ID){

        alertaService.delete(ID);
        return list();
    }


}
