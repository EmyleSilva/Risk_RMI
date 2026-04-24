package com.RiskRmi.enums;

public enum Territorios {
    BARATIE("Baratie"),
    ARLONG("Arlong Park"),
    FOOSHA("Foosha Village"),
    WHITE("White City"),
    SPIDER("Spider Miles"),
    OHARA("Ohara"),
    KANO("Kano Country"),
    GODVALLEY("God Valley"),
    WHOLECAKE("Whole Cake"),
    WANO("Wano"),
    ENIES("Enies Lobby"),
    SKYPIEA("Skypiea");

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
