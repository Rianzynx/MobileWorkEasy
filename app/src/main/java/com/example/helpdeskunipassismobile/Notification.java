package com.example.helpdeskunipassismobile;

public class Notification {
    private String titulo;
    private String descricao;

    public Notification(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
}
