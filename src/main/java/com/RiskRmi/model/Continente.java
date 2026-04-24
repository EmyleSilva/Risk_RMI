package com.RiskRmi.model;

import java.io.Serializable;
import java.util.List;

public class Continente implements Serializable {

    private String nome;
    private List<Territorio> territorios;
    private int bonus;

    public Continente(String nome, List<Territorio> territorios, int bonus) {
        this.nome = nome;
        this.territorios = territorios;
        this.bonus = bonus;
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

    public String getNomesTerritorios()  {
        String t = "";
        for (Territorio territorio : territorios) {
            t = t + territorio.getNome() + ", ";
        }
        return t;
    }

    public int getBonus() {
        return bonus;
    }

    @Override
    public String toString() {
        return "Continente{" +
                "nome='" + nome + '\'' +
                ", territorios=" + territorios +
                ", bonus=" + bonus +
                '}';
    }
}
