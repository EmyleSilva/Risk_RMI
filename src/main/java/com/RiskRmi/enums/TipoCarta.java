package com.RiskRmi.enums;

public enum TipoCarta {
    INFANTARIA("CARTA: Infantaria"),
    CAVALARIA("CARTA: Cavalaria");

    private String descricao;

    TipoCarta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
