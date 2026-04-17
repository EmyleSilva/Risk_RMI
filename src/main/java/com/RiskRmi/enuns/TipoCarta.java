package com.RiskRmi.enuns;

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

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
