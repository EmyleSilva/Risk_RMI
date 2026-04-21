package com.RiskRmi.enums;

public enum Territorios {
    BRASIL("Brasil"),
    VENEZUELA("Venezuela"),
    COLOMBIA("Colombia"),
    ARGELIA("Argelia"),
    EGITO("Egito"),
    CONGO("Congo"),
    QUENIA("Quenia"),
    ESPANHA("Espanha"),
    ALEMANHA("Alemanha"),
    CHINA("China"),
    JAPAO("Japao");

    private String nome;

    private Territorios(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
