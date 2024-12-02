package com.raphaelprojetos.Sentinel.entities;


import jakarta.persistence.*;
import org.springframework.data.annotation.Id;


@Entity
@Table(name = "usuarios")
public class Usuario {

    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Id
    private long Id;

    @Column(nullable = false)
    public String nome;

    @Column(nullable = false)
    private int senha;

    @Column(nullable = false)
    public boolean admin;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getSenha() {
        return senha;
    }

    public void setSenha(int senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }



}
