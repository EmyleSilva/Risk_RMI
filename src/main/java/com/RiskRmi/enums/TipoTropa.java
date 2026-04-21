package com.RiskRmi.enums;

public enum TipoTropa {
    INFANTARIA("Infantaria"),
    CAVALARIA("Cavalaria");

    private String nome;

    private TipoTropa(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
