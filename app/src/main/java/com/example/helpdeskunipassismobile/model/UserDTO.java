package com.example.helpdeskunipassismobile.model;

public class UserDTO {

    private String nome;

    private String avatar;

    // Construtor
    public UserDTO() {}

    public UserDTO(String nome, String avatar) {
        this.nome = nome;
        this.avatar = avatar;
    }

    // Getters e setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
