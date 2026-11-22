package com.financontrol.model;

public class Categoria {

    private Long id;
    private String nome;
    private String tipo;
    private Long usuarioId;

    public Categoria() {
    }

    public Categoria(Long id, String nome, String tipo, Long usuarioId) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.usuarioId = usuarioId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    @Override
    public String toString() {
        return nome;
    }
}
