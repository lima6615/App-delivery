package com.project.deliveryapp.activity.entities;

import java.io.Serializable;

public class Produto implements Serializable {

    private String usuarioId;
    private String nome;
    private String descricao;
    private Double price;

    public Produto() {
    }

    public Produto(String usuarioId, String nome, String descricao, Double price) {
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.descricao = descricao;
        this.price = price;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
