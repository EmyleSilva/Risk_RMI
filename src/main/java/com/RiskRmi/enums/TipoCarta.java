package com.RiskRmi.enums;

public enum TipoCarta {
    INFANTARIA("CARTA: Piratas"),
    CAVALARIA("CARTA: Marinha");

    private String descricao;

    TipoCarta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
