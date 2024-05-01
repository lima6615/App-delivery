package com.project.deliveryapp.activity.entities;

import java.io.Serializable;

public class Produto implements Serializable {

    private String id;
    private String nome;
    private String descricao;
    private String price;


    public Produto() {
    }

    public Produto(String id, String nome, String descricao, String price) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.price = price;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
