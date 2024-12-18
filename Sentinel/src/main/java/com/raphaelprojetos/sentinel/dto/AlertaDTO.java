package com.raphaelprojetos.sentinel.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

public class AlertaDTO {

    private Long id;
    private String codigo;
    private String titulo;
    public String tempoFormatado;
    public String descricao;

    public AlertaDTO(){

    }

    public AlertaDTO(String codigo, String titulo, LocalDateTime tempo, String descricao ){


        this.codigo = codigo;
        this.titulo = titulo;
        this.tempoFormatado = tempo != null ? tempo.toString() : "";
        this.descricao = descricao;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTempoFormatado() {
        return tempoFormatado;
    }

    public void setTempoFormatado(String tempoFormatado) {
        this.tempoFormatado = tempoFormatado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

}
