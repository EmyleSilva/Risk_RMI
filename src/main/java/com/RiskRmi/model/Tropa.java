package com.RiskRmi.model;

import com.RiskRmi.enuns.TipoTropa;

import java.io.Serializable;

public class Tropa implements Serializable {

    private TipoTropa tropa;
    private Integer valor;

    public Tropa(TipoTropa tropa, Integer valor) {
        this.tropa = tropa;
        this.valor = valor;
    }

    public TipoTropa getTropa() {
        return tropa;
    }

    public void setTropa(TipoTropa tropa) {
        this.tropa = tropa;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Tropa{" +
                "tropa=" + tropa +
                ", valor=" + valor +
                '}';
    }
}
