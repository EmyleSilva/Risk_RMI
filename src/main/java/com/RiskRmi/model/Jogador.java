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
    private List<Carta> cartas = new ArrayList<>();
    private ClientCallback clienteAssociado;

    public Jogador(String nome, ClientCallback clienteAssociado) {
        this.nome = nome;
        this.clienteAssociado = clienteAssociado;
    }

    public void adicionarCarta(Carta carta) {
        cartas.add(carta);
    }

    /**
     * Verifica se o jogador possui cartas suficientes e se atende ao critério de bonificação por cartas.
     * Se sim, retira as cartas da mão do jogador e as adiciona novamente no baralho.
     * @param cartasJogo Os tipos de carta que estão no jogo.
     * @param baralho O baralho do jogo.
     * @return true quando acontece a troca, caso contrário, false.
     * */
    public boolean retirarCartasBonus(Map<TipoCarta, Carta> cartasJogo, List<Carta> baralho) {
        Integer quantidadeCartas = quantidadeCartas();

        if (quantidadeCartas < 3) {
            throw new InvalidActionException("Você tem menos de 3 cartas para trocar!");
        }

        int quantInfantaria = 0;
        int quantCavalaria = 0;

        for (int i = 0; i < quantidadeCartas; i++) {
            if (cartas.get(i).getTipo() == TipoCarta.INFANTARIA) quantInfantaria++;
            else quantCavalaria++;
        }

        if (quantInfantaria >= 3) {
            removerCartas(TipoCarta.INFANTARIA, cartasJogo, baralho);
            return true;
        }else if (quantCavalaria >= 3) {
            removerCartas(TipoCarta.CAVALARIA, cartasJogo, baralho);
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
     * @param cartasJogo Os tipos de carta que estão no jogo.
     * @param baralho O baralho do jogo.
     * */
    public void removerCartas(TipoCarta tipo, Map<TipoCarta, Carta> cartasJogo, List<Carta> baralho) {
        for (int i = 0; i < 3; i++) {
            baralho.add(cartasJogo.get(tipo));
            cartas.remove(cartasJogo.get(tipo));
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
     * Busca uma lista de territorios juntamente com as tropas (de diferentes tipos) pertencentes a ele.
     * @param tropasJogo Tropas do Jogo.
     * @return Uma lista de territorios e suas tropas.
     * */
    public List<String> buscarTerritoriosTropas(Map<String, Tropa> tropasJogo) {
        List<String> territoriosTropas = new ArrayList<>();
        Tropa infantaria = tropasJogo.get(TipoTropa.INFANTARIA.getNome());
        Tropa cavalaria = tropasJogo.get(TipoTropa.CAVALARIA.getNome());

        for (Territorio t : territorios) {
            Integer i = t.getTropas().get(infantaria);
            Integer c = t.getTropas().get(cavalaria) * cavalaria.getValor();
            territoriosTropas.add("Território: " + t.getNome() + " | Tropas: infantaria(" + i + ") | cavalaria(" + c + ")");
        }

        return territoriosTropas;
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

    /**
     * Cria uma lista com os nomes de todas as cartas do jogador.
     * @return A lista com os nomes.
     * */
    public List<String> getCartasNomes() {
        List<String> cartas = new ArrayList<>();
        for (Carta c : this.cartas) {
            cartas.add(c.getDescricao());
        }

        return cartas;
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

    public List<Carta> getCartas() {
        return cartas;
    }

    public void setCartas(List<Carta> cartas) {
        this.cartas = cartas;
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
