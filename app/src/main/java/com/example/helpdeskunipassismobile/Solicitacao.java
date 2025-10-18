package com.example.helpdeskunipassismobile;

public class Solicitacao {
    private String titulo;
    private String status;
    private String data;
    private String prioridade;

    // Construtor completo
    public Solicitacao(String titulo, String status, String data, String prioridade) {
        this.titulo = titulo;
        this.status = status;
        this.data = data;
        this.prioridade = prioridade;
    }

    // Getters
    public String getTitulo() { return titulo; }
    public String getStatus() { return status; }
    public String getData() { return data; }
    public String getPrioridade() { return prioridade; }
}


