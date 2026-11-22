package com.financontrol.model;

import java.time.LocalDate;

public class Despesa extends Transacao {

    public Despesa(Long id, String descricao, double valor, LocalDate data, Categoria categoria, Long usuarioId) {
        super(id, descricao, valor, data, categoria, usuarioId);
    }

    @Override
    public double calcularImpactoNoSaldo() {
        return -getValor();
    }

    @Override
    public String getTipo() {
        return "DESPESA";
    }
}
