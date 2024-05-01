package com.project.deliveryapp.activity.entities;

import java.io.Serializable;
import java.util.List;

public class Pedido implements Serializable {

    private String idUsuario;
    private String idEmpresa;
    private String nome;
    private String email;
    private String telefone;
    private Endereco endereco;
    private String status;
    private Double taxa;
    private Integer metodoPagamento;
    private String observacao;
    private List<ItemPedido> itens;
    private Double valorTotal = 0.0;

    public Pedido() {
    }

    public Pedido(String idUsuario, String idEmpresa, String nome,
                  String email, String telefone, Endereco endereco, String status,
                  Double taxa, Integer metodoPagamento, String observacao) {
        this.idUsuario = idUsuario;
        this.idEmpresa = idEmpresa;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.status = status;
        this.taxa = taxa;
        this.metodoPagamento = metodoPagamento;
        this.observacao = observacao;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTaxa() {
        return taxa;
    }

    public void setTaxa(Double taxa) {
        this.taxa = taxa;
    }

    public Integer getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(Integer metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Double total() {
        for (ItemPedido pedido : itens) {
            valorTotal += pedido.subTotal();
        }
        valorTotal += taxa;
        return valorTotal;
    }
}
