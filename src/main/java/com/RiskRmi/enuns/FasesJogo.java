package com.RiskRmi.enuns;

public enum FasesJogo {
    POSICIONAMETO_INICAL("POSICIONAMENTO_INICIAL"),
    POSCIONAMENTO("POSICIONAMENTO"),
    ATAQUE("ATAQUE"),
    MOVIMENTACAO("MOVIMENTACAO");

    private String descricao;

    FasesJogo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
