package com.project.deliveryapp.activity.entities;

import java.io.Serializable;

public class Produto implements Serializable {

    private String id;
    private String nome;
    private String descricao;
    private String price;
    private String usuarioId;

    public Produto() {
    }

    public Produto(String usuarioId, String nome, String descricao, String price) {
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.descricao = descricao;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
}
