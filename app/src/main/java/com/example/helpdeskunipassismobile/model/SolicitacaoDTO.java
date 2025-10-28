package com.example.helpdeskunipassismobile.model;

public class SolicitacaoDTO {
    private int id;
    private String titulo;
    private String descricao;
    private String observacoes;
    private String categoria;
    private String prioridade;
    private String status;
    private String data;

    private Long idFuncionario; // <-- adicione isto

    public SolicitacaoDTO() {}

    public SolicitacaoDTO(int id, String titulo, String descricao, String observacoes,
                          String categoria, String prioridade, String status, String data, Long idFuncionario) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.observacoes = observacoes;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.status = status;
        this.data = data;
        this.idFuncionario = idFuncionario;
    }

    // Getters e setters
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getObservacoes() { return observacoes; }
    public String getCategoria() { return categoria; }
    public String getPrioridade() { return prioridade; }
    public String getStatus() { return status; }
    public String getData() { return data; }
    public Long getIdFuncionario() { return idFuncionario; } // <-- adicione isto

    public void setId(int id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public void setStatus(String status) { this.status = status; }
    public void setData(String data) { this.data = data; }
    public void setIdFuncionario(Long idFuncionario) { this.idFuncionario = idFuncionario; } // <-- adicione isto
}
