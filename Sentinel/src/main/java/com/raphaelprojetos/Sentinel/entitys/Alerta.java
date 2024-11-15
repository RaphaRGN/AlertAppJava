package com.raphaelprojetos.Sentinel.entitys;

import jakarta.persistence.*;

@Entity
@Table (name = "Alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long ID;
    public String codigo;
    public String titulo;
    public String descricao;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
