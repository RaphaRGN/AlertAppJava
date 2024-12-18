package com.raphaelprojetos.sentinel.services;

import com.raphaelprojetos.sentinel.entities.Usuario;
import com.raphaelprojetos.sentinel.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    UsuarioRepository usuarioRepository;

     public UsuarioService (UsuarioRepository usuarioRepository){

        this.usuarioRepository = usuarioRepository;

    }

    public Usuario create(Usuario usuario){

        return usuarioRepository.save(usuario);

    }

    public List<Usuario> list() {

        Sort sort = Sort.by("ID").descending().and(Sort.by("titulo").ascending());
        return usuarioRepository.findAll(sort);

    }

    public Usuario update (Usuario usuario){

        return usuarioRepository.save(usuario);

    }

    public List<Usuario> delete (Long Id){

        usuarioRepository.deleteById(Id);
        return list();

    }

}
