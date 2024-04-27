package com.project.deliveryapp.activity.entities;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String nome;
    private String email;
    private String senha;
    private String tipoConta;

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, String tipoConta) {
        this.nome = nome;
        this.email = email;
        this.tipoConta = tipoConta;
        this.senha = senha;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(String tipoConta) {
        this.tipoConta = tipoConta;
    }
}
