package com.RiskRmi.server;

import com.RiskRmi.enuns.FasesJogo;
import com.RiskRmi.enuns.Territorios;
import com.RiskRmi.exceptions.InvalidActionException;
import com.RiskRmi.model.Jogador;
import com.RiskRmi.model.Territorio;
import com.RiskRmi.model.Tropa;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Validate {

    private Map<String, Tropa> tropas;
    private Map<String, Territorio> territorios;

    public Validate(Map<String, Tropa> tropas, Map<String, Territorio> territorios) {
        this.tropas = tropas;
        this.territorios = territorios;
    }

    public void validarTurnoJogador(List<Jogador> jogadores, int jogadorId, int indexAtual) {
        if (jogadores.get(indexAtual).getId() != jogadorId) {
            throw new InvalidActionException("\nNão é sua vez de jogar!\n");
        }
    }

    public void validarTerritorioJogador(Territorio territorio, Jogador jogador) {
        if (territorio == null) {
            throw new InvalidActionException("Território Inválido\n");
        }

        if (territorio.getDono() != jogador) {
            throw new InvalidActionException("\nEste território não é seu!\n");
        }
    }

    public void validarTropasDisponiveis(int quantidadeTropas, Jogador jogador) {
        if (quantidadeTropas > jogador.getTropasDisponiveis()) {
            throw new InvalidActionException("\nQuantidade de Tropas Disponíveis Insuficiente\n");
        }
    }

    public void validarFaseAtual(Stack<FasesJogo> fasesPorTurno, FasesJogo fasesJogo) {
        if (fasesJogo != fasesPorTurno.peek()) {
            throw new InvalidActionException("Ação inválida! O turno atual é: " + fasesPorTurno.peek().getDescricao());
        }
    }

    public void validarVizinho(Territorio origem, Territorio destino) {
        boolean encontrouVizinho = false;
        Territorios destinoNome = destino.getNome();

        for (Territorios t : origem.getVizinhos()) {
            if (t == destinoNome) {
                encontrouVizinho = true;
                break;
            }
        }

        if (!encontrouVizinho) {
            throw new InvalidActionException("Você não pode atacar " + destinoNome.getNome() + ". Não são vizinhos!");
        }
    }

    public void validarQuantidadeTropas(Territorio territorio) {
        /** O ataque não pode ocorrer de um território que possui apenas 1 tropa. */
        if (territorio.getTotalTropas(tropas) == 1) {
            throw new InvalidActionException("Território só possui 1 tropa.");
        }
    }

    public void validarQuantidadeTropas(Integer quantidadeTropas, Territorio territorio) {
        final Integer totalTropas = territorio.getTotalTropas(tropas);
        if (totalTropas == 1) {
            throw new InvalidActionException("Território só possui 1 tropa.");
        }

        if (totalTropas - quantidadeTropas < 1) {
            throw new InvalidActionException("Quantidade de tropas insuficientes!");
        }
    }

    public void validarDonoDestino(Jogador jogador, Territorio destino) {
        if (destino.getDono() == jogador) {
            throw new InvalidActionException("Você não pode atacar seu próprio território!");
        }
    }
}
