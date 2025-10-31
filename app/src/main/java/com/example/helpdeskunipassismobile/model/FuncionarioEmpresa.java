package com.example.helpdeskunipassismobile.model;

import com.google.gson.annotations.SerializedName;

public class FuncionarioEmpresa {

    @SerializedName("id_funcionario")
    private Long id;

    @SerializedName("nome")
    private String nome;

    @SerializedName("senha")
    private String senha;

    @SerializedName("cpf")
    private String cpf;

    @SerializedName("setor")
    private String setor;

    @SerializedName("email")
    private String email;

    @SerializedName("telefone")
    private String telefone;

    @SerializedName("endereco")
    private String endereco;

    @SerializedName("data_nascimento")
    private String dataNascimento;

    @SerializedName("data_admissao")
    private String dataAdmissao;

    @SerializedName("status_funcionario")
    private String statusFuncionario;

    // ===== Getters e Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(String dataAdmissao) { this.dataAdmissao = dataAdmissao; }

    public String getStatusFuncionario() { return statusFuncionario; }
    public void setStatusFuncionario(String statusFuncionario) { this.statusFuncionario = statusFuncionario; }
}
