package com.raphaelprojetos.Sentinel.controller;

import com.raphaelprojetos.Sentinel.entities.Usuario;
import com.raphaelprojetos.Sentinel.services.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){

        this.usuarioService = usuarioService;

    }

    @PostMapping
    @ResponseBody
    public Usuario create (Usuario usuario){

        return usuarioService.create(usuario);

    }

    @GetMapping
    @ResponseBody
    public List<Usuario> list(){

        return usuarioService.list();

    }

    @PutMapping
    @ResponseBody
    public Usuario update (@RequestBody Usuario usuario){

        return usuarioService.update(usuario);

    }

    @DeleteMapping("{Id}")
    @ResponseBody
    public List<Usuario> delete(@PathVariable ("Id") long Id){

        usuarioService.delete(Id);
        return list();
    }
}
