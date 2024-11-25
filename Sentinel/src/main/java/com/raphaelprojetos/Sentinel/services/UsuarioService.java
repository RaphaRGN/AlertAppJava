package com.raphaelprojetos.Sentinel.services;

import com.raphaelprojetos.Sentinel.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    UsuarioRepository usuarioRepository;

     public UsuarioService (UsuarioRepository usuarioRepository){

        this.usuarioRepository = usuarioRepository;

    }




}
