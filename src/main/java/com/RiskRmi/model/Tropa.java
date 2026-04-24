package com.RiskRmi.model;

import com.RiskRmi.enums.TipoTropa;

import java.io.Serializable;

public class Tropa implements Serializable {

    private TipoTropa tropa;
    private Integer valor;

    public Tropa(TipoTropa tropa, Integer valor) {
        this.tropa = tropa;
        this.valor = valor;
    }

    public Integer getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return "Tropa{" +
                "tropa=" + tropa +
                ", valor=" + valor +
                '}';
    }
}
