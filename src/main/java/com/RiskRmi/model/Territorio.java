package com.RiskRmi.model;

import com.RiskRmi.enuns.Territorios;
import com.RiskRmi.enuns.TipoTropa;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Territorio implements Serializable {

    private Territorios nome;
    private Jogador dono;
    private List<Territorios> vizinhos;
    private Map<Tropa, Integer> tropas;

    public Territorio(Territorios nome, List<Territorios> vizinhos) {
        this.nome = nome;
        this.vizinhos = vizinhos;
    }

    /** Métodos do Jogo */

    /**
     * Inicializa as tropas do território de acordo com o tipo de tropas criadas no jogo.
     * Por padrão, todos os territórios começam com 0 tropas, até que a distribuição inicial comece.
     *
     * TODO: Todos os territórios já podem começar com 1 tropa, e na parte de distribuir territórios não seria
     *       mais necessário chamar adicionarTropas()
     *
     * @param tropasDoJogo As tropas criadas no jogo.
     * */
    public void inicializarTropas(Map<String, Tropa> tropasDoJogo) {
        tropas = new HashMap<>();

        for (String t : tropasDoJogo.keySet()) {
            tropas.put(tropasDoJogo.get(t), 0);
        }

    }

    /**
     * Adiciona tropas em um território, realizando o agrupamento sempre que possivel para cavalarias.
     * Se a quantidade de tropas a ser inserida em um territorio for maior que o valor de uma cavalaria,
     * adiciona em cavalarias o total correspondente a divisão inteira das tropas pelo valor da cavalaria.
     * Depois, calcula o resto dessa divisão e adiciona o resultado em infantarias.
     *
     * TODO: Após inclusões, verificar o map de tropas para saber se a infantaria pode ser reorganizada para
     *       se tornarem cavalarias.
     *
     * @param tropas Um map com as tropas criadas no jogo.
     * @param quantidade A quantidade total de tropas que o jogador deseja posicionar no território.
     * */
    public void adicionarTropas(Map<String, Tropa> tropas, Integer quantidade) {
        Tropa infantaria = tropas.get(TipoTropa.INFANTARIA.getNome());
        Tropa cavalaria = tropas.get(TipoTropa.CAVALARIA.getNome());

        if (quantidade < infantaria.getValor()) {
            this.tropas.put(infantaria, quantidadeTropasAtuais(infantaria)+quantidade);
        }else {
            /** Formar cavalarias */
            Integer quantCavalaria = quantidade / cavalaria.getValor();
            Integer quantInfantaria = quantidade % cavalaria.getValor();

            this.tropas.put(infantaria, quantidadeTropasAtuais(infantaria)+quantInfantaria);
            this.tropas.put(cavalaria, quantidadeTropasAtuais(cavalaria)+quantCavalaria);
        }
    }

    /** Métodos Auxiliares */
    public Integer quantidadeTropasAtuais(Tropa key) {
        return tropas.get(key);
    }

    /** Getters e Setters */
    public Territorios getNome() {
        return nome;
    }

    public void setNome(Territorios nome) {
        this.nome = nome;
    }

    public Jogador getDono() {
        return dono;
    }

    public void setDono(Jogador dono) {
        this.dono = dono;
    }

    public List<Territorios> getVizinhos() {
        return vizinhos;
    }

    public void setVizinhos(List<Territorios> vizinhos) {
        this.vizinhos = vizinhos;
    }

    public Map<Tropa, Integer> getTropas() {
        return tropas;
    }

    public void setTropas(Map<Tropa, Integer> tropas) {
        this.tropas = tropas;
    }

    @Override
    public String toString() {
        return "Territorio{" +
                "nome=" + nome +
                ", dono=" + dono +
                ", vizinhos=" + vizinhos +
                ", tropas=" + tropas +
                '}';
    }
}
