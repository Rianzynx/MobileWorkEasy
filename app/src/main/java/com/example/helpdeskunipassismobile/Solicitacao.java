package com.example.helpdeskunipassismobile;

import com.google.gson.annotations.SerializedName;

public class Solicitacao {

    @SerializedName("id")
    private int id;

    @SerializedName("descricao")
    private String descricao;

    @SerializedName("status")
    private String status;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("prioridade")
    private String prioridade;

    @SerializedName("data")
    private String data;

    @SerializedName("observacoes")
    private String observacoes;

    @SerializedName("categoria")
    private String categoria;

    public Solicitacao() {}

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() {
        if (titulo != null && !titulo.isEmpty()) return titulo;
        else if (descricao != null && !descricao.isEmpty()) return descricao;
        else return "Sem título";
    }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getData() { return data != null ? data : "Data indefinida"; }
    public void setData(String data) { this.data = data; }

    public String getCategoria() { return categoria != null ? categoria : "Categoria indefinida"; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getPrioridade() {
        return (prioridade != null && !prioridade.isEmpty()) ? prioridade : "Indefinida";
    }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getStatus() {
        return (status != null && !status.isEmpty()) ? status : "Indefinido";
    }
    public void setStatus(String status) { this.status = status; }
}
