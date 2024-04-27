package com.project.deliveryapp.activity.entities;

import java.io.Serializable;

public class Endereco implements Serializable {

    private String idUsuario;
    private String cep;
    private String bairro;
    private String rua;
    private String cidade;

    public Endereco() {
    }

    public Endereco(String idUsuario, String cep, String bairro, String rua, String cidade) {
        this.idUsuario = idUsuario;
        this.cep = cep;
        this.bairro = bairro;
        this.rua = rua;
        this.cidade = cidade;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
}
