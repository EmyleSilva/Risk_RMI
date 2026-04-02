package com.RiskRmi.model;

import com.RiskRmi.enuns.Territorios;

import java.io.Serializable;
import java.util.List;

public class Continente implements Serializable {

    private String nome;
    private List<Territorio> territorios;
    private int bonus;

    public Continente(String nome, List<Territorio> territorios, int bonus) {
        this.nome = nome;
        this.territorios = territorios;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Territorio> getTerritorios() {
        return territorios;
    }

    public void setTerritorios(List<Territorio> territorios) {
        this.territorios = territorios;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
