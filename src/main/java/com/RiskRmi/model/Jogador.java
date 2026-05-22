package com.RiskRmi.model;

import com.RiskRmi.Rmi.ClientCallback;
import com.RiskRmi.enums.TipoCarta;
import com.RiskRmi.enums.TipoTropa;
import com.RiskRmi.exceptions.InvalidActionException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Jogador implements Serializable {

    private String nome;
    private int id;
    private List<Territorio> territorios = new ArrayList<>();
    private Map<TipoTropa, Integer> tropas;
    private int tropasDisponiveis;
    private List<TipoCarta> cartas = new ArrayList<>();
    private ClientCallback clienteAssociado;

    public Jogador(String nome, ClientCallback clienteAssociado) {
        this.nome = nome;
        this.clienteAssociado = clienteAssociado;
    }

    public void adicionarCarta(TipoCarta carta) {
        cartas.add(carta);
    }

    /**
     * Verifica se o jogador possui cartas suficientes e se atende ao critério de bonificação por cartas.
     * Se sim, retira as cartas da mão do jogador e as adiciona novamente no baralho.
     * @param baralho O baralho do jogo.
     * @return true quando acontece a troca, caso contrário, false.
     * */
    public boolean retirarCartasBonus(List<TipoCarta> baralho) {
        Integer quantidadeCartas = quantidadeCartas();

        if (quantidadeCartas < 3) {
            throw new InvalidActionException("Você tem menos de 3 cartas para trocar!");
        }

        int quantInfantaria = 0;
        int quantCavalaria = 0;

        for (int i = 0; i < quantidadeCartas; i++) {
            if (cartas.get(i) == TipoCarta.INFANTARIA) quantInfantaria++;
            else quantCavalaria++;
        }

        if (quantInfantaria >= 3) {
            removerCartas(TipoCarta.INFANTARIA, baralho);
            return true;
        }else if (quantCavalaria >= 3) {
            removerCartas(TipoCarta.CAVALARIA, baralho);
            return true;
        }

        return false;
    }

    /**
     * @return A quantidade total de cartas do jogador.
     * */
    public Integer quantidadeCartas() {
        return cartas.size();
    }

    /**
     * Remove 3 cartas da mão do jogador e as adiciona novamente no baralho do jogo.
     * @param tipo O tipo de carta que está sendo removida.
     *  cartasJogo Os tipos de carta no jogo.
     * @param baralho O baralho do jogo.
     * */
    public void removerCartas(TipoCarta tipo, List<TipoCarta> baralho) {
        for (int i = 0; i < 3; i++) {
            baralho.add(tipo);
            cartas.remove(tipo);
        }
    }

    /**
     * Busca todos os territorios do jogador.
     * @return Uma lista com o nome de todos os territorios.
     * */
    public List<String> buscarTerritorios() {
        List<String> nomesTerritorios = new ArrayList<>();
        for (Territorio t : territorios) {
            nomesTerritorios.add(t.getNome());
        }
        return nomesTerritorios;
    }

    /**
     * Busca uma lista de territorios com as tropas (de diferentes tipos) pertencentes a ele.
     *
     * @return Uma lista de territorios e as suas tropas.
     * */
    public List<String> buscarTerritoriosTropas() {
        List<String> territoriosTropas = new ArrayList<>();

        for (Territorio t : territorios) {
            Integer i = t.getTropas().get(TipoTropa.INFANTARIA);
            Integer c = t.getTropas().get(TipoTropa.CAVALARIA);
            territoriosTropas.add(t.getNome() + "{infantaria(" + i + ") cavalaria(" + c + ")}");
        }

        return territoriosTropas;
    }

    /** Métodos para o jogo */

    /**
     * Adiciona um território na lista de territórios.
     *
     * @param territorio O novo território do jogador.
     * */
    public void adicionarTerritorio(Territorio territorio) {
        territorios.add(territorio);
    }

    /**
     * Cria uma lista com os nomes de todas as cartas do jogador.
     * @return A lista com os nomes.
     * */
    public List<String> getCartasNomes() {
        List<String> cartas = new ArrayList<>();
        for (TipoCarta c : this.cartas) {
            cartas.add(c.getDescricao());
        }

        return cartas;
    }

    public Integer buscarTotalTropasJogador() {
        Integer quantidade = 0;

        for (Territorio t : territorios) {
            quantidade += t.getTropas().get(TipoTropa.INFANTARIA);
            quantidade += t.getTropas().get(TipoTropa.CAVALARIA) * TipoTropa.CAVALARIA.getValor();
        }

        return quantidade;
    }


    /** Getters e Setters */
    public String getNome() {
        return nome;
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

    public int getTropasDisponiveis() {
        return tropasDisponiveis;
    }

    public void setTropasDisponiveis(int quantidadeTropasDisponiveis) {
        this.tropasDisponiveis = quantidadeTropasDisponiveis;
    }

    public ClientCallback getClienteAssociado() {
        return clienteAssociado;
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
