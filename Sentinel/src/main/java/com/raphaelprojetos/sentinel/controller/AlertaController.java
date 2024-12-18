package com.raphaelprojetos.sentinel.controller;

import com.raphaelprojetos.sentinel.entities.Alerta;
import com.raphaelprojetos.sentinel.services.AlertaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService){

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

    @DeleteMapping("{Id}")
    @ResponseBody
    public List<Alerta> delete(@PathVariable ("Id") long Id){

        alertaService.delete(Id);
        return list();
    }


}
