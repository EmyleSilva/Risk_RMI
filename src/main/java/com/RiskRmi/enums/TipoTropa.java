package com.RiskRmi.enums;

public enum TipoTropa {
    INFANTARIA(1, "Infantaria"),
    CAVALARIA(5, "Cavalaria");

    private String nome;
    private Integer valor;

    private TipoTropa(Integer valor, String nome) {
        this.valor = valor;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public Integer getValor() {
        return valor;
    }
}
