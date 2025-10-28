package com.example.helpdeskunipassismobile;

import com.google.gson.annotations.SerializedName;

public class FuncionarioEmpresa {
    @SerializedName("id_funcionario")
    private int idFuncionario;

    @SerializedName("nome")
    private String nome;

    @SerializedName("email")
    private String email;

    // Getters e Setters
    public int getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(int idFuncionario) { this.idFuncionario = idFuncionario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
