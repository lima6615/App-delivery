package com.project.deliveryapp.activity.entities;

import java.io.Serializable;

public class Empresa implements Serializable {

    private String idUsuario;
    private String urlImagem;
    private String nome;
    private String tempo;
    private String categoria;
    private String taxa;

    public Empresa() {
    }

    public Empresa(String idUsuario, String urlImagem, String nome, String tempo, String categoria, String taxa) {
        this.idUsuario = idUsuario;
        this.urlImagem = urlImagem;
        this.nome = nome;
        this.tempo = tempo;
        this.categoria = categoria;
        this.taxa = taxa;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTaxa() {
        return taxa;
    }

    public void setTaxa(String taxa) {
        this.taxa = taxa;
    }
}
