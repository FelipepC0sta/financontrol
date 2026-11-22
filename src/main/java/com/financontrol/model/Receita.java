package com.financontrol.model;

import java.time.LocalDate;

public class Receita extends Transacao {

    public Receita(Long id, String descricao, double valor, LocalDate data, Categoria categoria, Long usuarioId) {
        super(id, descricao, valor, data, categoria, usuarioId);
    }

    @Override
    public double calcularImpactoNoSaldo() {
        return getValor();
    }

    @Override
    public String getTipo() {
        return "RECEITA";
    }
}
