package com.raphaelprojetos.Sentinel.entitys;

import jakarta.persistence.*;

@Entity
@Table (name = "Usuarios")
public class Usuario {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    public long ID;
    public String nome;
    public boolean podeMandarAlertas;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isPodeMandarAlertas() {
        return podeMandarAlertas;
    }

    public void setPodeMandarAlertas(boolean podeMandarAlertas) {
        this.podeMandarAlertas = podeMandarAlertas;
    }



}
