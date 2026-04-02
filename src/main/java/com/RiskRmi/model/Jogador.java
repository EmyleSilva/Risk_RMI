package com.RiskRmi.model;

import com.RiskRmi.enuns.TipoTropa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Jogador implements Serializable {

    private String nome;
    private int id;
    private List<Territorio> territorios;
    private Map<TipoTropa, Integer> tropas;
    private int tropasDisponiveis;

    public Jogador(String nome) {
        this.nome = nome;
        territorios = new ArrayList<>();
    }

    /** Metódos para o jogo */

    /**
     * Adiciona um território na lista de territórios.
     *
     * @param territorio O novo território do jogador.
     * */
    public void adicionarTerritorio(Territorio territorio) {
        territorios.add(territorio);
    }


    /** Getters e Setters */
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Territorio> getTerritorios() {
        return territorios;
    }

    public void setTerritorios(List<Territorio> territorios) {
        this.territorios = territorios;
    }

    public Map<TipoTropa, Integer> getTropas() {
        return tropas;
    }

    public void setTropas(Map<TipoTropa, Integer> tropas) {
        this.tropas = tropas;
    }

    public int getTropasDisponiveis() {
        return tropasDisponiveis;
    }

    public void setTropasDisponiveis(int quantidadeTropasDisponiveis) {
        this.tropasDisponiveis = quantidadeTropasDisponiveis;
    }

    @Override
    public String toString() {
        return "Jogador{" +
                "nome='" + nome + '\'' +
                ", id=" + id +
                ", territorios=" + territorios +
                ", tropas=" + tropas +
                ", tropasDisponiveis=" + tropasDisponiveis +
                '}';
    }
}
