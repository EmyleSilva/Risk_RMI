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
    private Map<TipoTropa, Integer> tropas;

    public Territorio(Territorios nome, List<Territorios> vizinhos) {
        this.nome = nome;
        this.vizinhos = vizinhos;
    }

    /** Métodos do Jogo */

    /**
     * Inicializa as tropas do território conforme o tipo de tropas criadas no jogo.
     * Por padrão, todos os territórios começam com 0 tropas, até que a distribuição inicial comece.
     *
     * @param tropasDoJogo As tropas criadas no jogo.
     * */
    public void inicializarTropas(List<TipoTropa> tropasDoJogo) {
        tropas = new HashMap<>();

        for (TipoTropa t: tropasDoJogo) {
            tropas.put(t, 0);
        }
    }

    /**
     * Adiciona tropas em um território.
     * Sempre adiciona todas as tropas em infantaria, e depois reorganiza para distribuir
     * corretamente entre infantaria e cavalaria.
     *
     * @param quantidade A quantidade total de tropas que o jogador deseja posicionar no território.
     * */
    public void adicionarTropas(Integer quantidade) {
        Integer quantCavalaria = 0;
        Integer quantInfantaria = getTotalTropas()+quantidade;
        this.tropas.put(TipoTropa.INFANTARIA, quantInfantaria);
        this.tropas.put(TipoTropa.CAVALARIA, quantCavalaria);

        reorganizarTropas();
    }

    /**
     * Calcula o total de tropas no territorio.
     * @return O total de tropas.
     * */
    public Integer getTotalTropas() {
        return this.tropas.get(TipoTropa.INFANTARIA) * TipoTropa.INFANTARIA.getValor() +
                this.tropas.get(TipoTropa.CAVALARIA) * TipoTropa.CAVALARIA.getValor();
    }

    /**
     * Retira a quantidade de tropas perdidas do território.
     * Reorganiza todas as tropas novamente para que a distribuição
     * entre infantaria e cavalaria permaneça correta.
     *
     * @param quantidade A quantidade de tropas perdidas no território.
     * */
    public void retirarTropas(int quantidade) {
        Integer totalTropas = getTotalTropas();

        Integer quantidadeInfantaria = this.tropas.get(TipoTropa.INFANTARIA);
        Integer quantidadeCavalaria = this.tropas.get(TipoTropa.CAVALARIA);

        //Para debug no servidor
        System.out.println("\nANTES: (infantaria) - " + quantidadeInfantaria + " (cavalaria) - " + quantidadeCavalaria);

        quantidadeInfantaria = totalTropas - quantidade;
        quantidadeCavalaria = 0;

        this.tropas.put(TipoTropa.INFANTARIA, quantidadeInfantaria);
        this.tropas.put(TipoTropa.CAVALARIA, quantidadeCavalaria);

        reorganizarTropas();
    }

    /**
     * Verifica se o território ficou sem nenhuma tropa.
     * Caso sim, o território foi capturado pelo jogador que realizou o último ataque.
     *
     * @return true se o território foi capturado e false caso contrário.
     * */
    public boolean verificarCaptura() {
        return getTotalTropas() == 0;
    }

    /** Métodos Auxiliares */
    public Integer quantidadeTropasAtuais(TipoTropa key) {
        return tropas.get(key);
    }

    /**
     * Reordena as tropas entre cavalaria e infantaria para melhor distribuição.
     * */
    public void reorganizarTropas() {
        Integer totalTropas = getTotalTropas();

        Integer quantidadeCavalaria = totalTropas / TipoTropa.CAVALARIA.getValor();
        Integer quantidadeInfantaria = totalTropas % TipoTropa.CAVALARIA.getValor();

        this.tropas.put(TipoTropa.INFANTARIA, quantidadeInfantaria);
        this.tropas.put(TipoTropa.CAVALARIA, quantidadeCavalaria);
    }

    /** Getters e Setters */
    public String getNome() {
        return nome.getNome();
    }

    public Territorios getNomeEnum() {
        return nome;
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

    public Map<TipoTropa, Integer> getTropas() {
        return tropas;
    }

    public String exibirTropas(List<TipoTropa> tropasJogo) {
        String mensagem = "";

        for (TipoTropa t : tropasJogo) {
            mensagem = mensagem + "{" + t.getNome() + "(" + tropas.get(t) + ")} ";
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
