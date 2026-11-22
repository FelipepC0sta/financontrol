package com.financontrol.model;

import java.time.LocalDate;

public abstract class Transacao {

    private Long id;
    private String descricao;
    private double valor;
    private LocalDate data;
    private Categoria categoria;
    private Long usuarioId;

    protected Transacao(Long id, String descricao, double valor, LocalDate data, Categoria categoria, Long usuarioId) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.categoria = categoria;
        this.usuarioId = usuarioId;
    }

    public abstract double calcularImpactoNoSaldo();

    public abstract String getTipo();

    public static Transacao criar(Long id, String tipo, String descricao, double valor,
                                  LocalDate data, Categoria categoria, Long usuarioId) {
        if ("RECEITA".equalsIgnoreCase(tipo)) {
            return new Receita(id, descricao, valor, data, categoria, usuarioId);
        }
        return new Despesa(id, descricao, valor, data, categoria, usuarioId);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}
