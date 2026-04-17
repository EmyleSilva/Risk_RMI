package com.RiskRmi.model;

import com.RiskRmi.enuns.TipoCarta;

import java.io.Serializable;

public class Carta implements Serializable {

    private TipoCarta tipo;

    public Carta(TipoCarta tipo) {
        this.tipo = tipo;
    }

    public TipoCarta getTipo() {
        return tipo;
    }

    public void setTipo(TipoCarta tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return this.tipo.getDescricao();
    }
}
