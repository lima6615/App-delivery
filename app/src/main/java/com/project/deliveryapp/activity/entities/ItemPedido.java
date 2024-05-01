package com.project.deliveryapp.activity.entities;

import java.io.Serializable;

public class ItemPedido implements Serializable {

    private String idProduto;
    private String nomeProduto;
    private Integer quantidade;
    private String descricao;
    private Double preco;

    public ItemPedido() {
    }

    public ItemPedido(String idProduto, String nomeProduto, Integer quantidade, String descricao, Double preco) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.descricao = descricao;
        this.preco = preco;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Double subTotal(){
        return quantidade * preco;
    }
}
