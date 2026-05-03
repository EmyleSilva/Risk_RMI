package com.RiskRmi.model;

import com.RiskRmi.enums.Territorios;
import com.RiskRmi.enums.TipoTropa;

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

    /**
     * Calcula o total de tropas no territorio.
     * @param tropas Tropas do jogo.
     * @return O total de tropas.
     * */
    public Integer getTotalTropas(Map<String, Tropa> tropas) {
        Tropa infantaria = tropas.get(TipoTropa.INFANTARIA.getNome());
        Tropa cavalaria = tropas.get(TipoTropa.CAVALARIA.getNome());

        return this.tropas.get(infantaria) + (this.tropas.get(cavalaria) * cavalaria.getValor());
    }

    /**
     * Retira a quantidade de tropas perdidas do território.
     * Reorganiza todas as tropas novamente para que fique equilibrado entre infantaria e
     * cavalaria.
     *
     * @param tropas Todas as tropas do jogo.
     * @param quantidade A quantidade de tropas perdidas no território.
     * */
    public void retirarTropas(Map<String, Tropa> tropas, int quantidade) {
        Tropa infantaria = tropas.get(TipoTropa.INFANTARIA.getNome());
        Tropa cavalaria = tropas.get(TipoTropa.CAVALARIA.getNome());
        Integer totalTropas = getTotalTropas(tropas);

        Integer quantidadeInfantaria = this.tropas.get(infantaria);
        Integer quantidadeCavalaria = this.tropas.get(cavalaria);
        System.out.println("\nANTES: (infantaria) - " + quantidadeInfantaria + " (cavalaria) - " + quantidadeCavalaria);

        quantidadeInfantaria = totalTropas - quantidade;
        quantidadeCavalaria = 0;

        this.tropas.put(infantaria, quantidadeInfantaria);
        this.tropas.put(cavalaria, quantidadeCavalaria);

        reorganizarTropas(tropas);
    }

    /**
     * Verifica se o território ficou sem nenhuma tropa.
     * Caso sim, o território foi capturado pelo jogador que realizou o último ataque.
     *
     * @param tropas Todas as tropas do jogo.
     * @return true se o território foi capturado e false caso contrário.
     * */
    public boolean verificarCaptura(Map<String, Tropa> tropas) {
        return getTotalTropas(tropas) == 0;
    }

    /** Métodos Auxiliares */
    public Integer quantidadeTropasAtuais(Tropa key) {
        return tropas.get(key);
    }

    /**
     * Reordena as tropas entre cavalaria e infantaria para melhor distribuição.
     *
     * @param tropas Todas as tropas do jogo.
     * */
    public void reorganizarTropas(Map<String, Tropa> tropas) {
        Integer totalTropas = getTotalTropas(tropas);
        Tropa infantaria = tropas.get(TipoTropa.INFANTARIA.getNome());
        Tropa cavalaria = tropas.get(TipoTropa.CAVALARIA.getNome());

        Integer quantidadeCavalaria = totalTropas / cavalaria.getValor();
        Integer quantidadeInfantaria = totalTropas % cavalaria.getValor();

        this.tropas.put(infantaria, quantidadeInfantaria);
        this.tropas.put(cavalaria, quantidadeCavalaria);
    }

    /** Getters e Setters */
    public String getNome() {
        return nome.getNome();
    }

    public Territorios getNomeEnum() {
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

    public Map<Tropa, Integer> getTropas() {
        return tropas;
    }

    public String exibirTropas(Map<String, Tropa> tropasJogo) {
        String mensagem = "";

        for (String s : tropasJogo.keySet()) {
            mensagem = mensagem + "{" + s + "(" + tropas.get(tropasJogo.get(s)) + ")} ";
        }
        return mensagem;
    }

    @Override
    public String toString() {
        return "Territorio{" +
                "nome=" + nome +
                ", vizinhos=" + vizinhos +
                ", tropas=" + tropas +
                '}';
    }
}
