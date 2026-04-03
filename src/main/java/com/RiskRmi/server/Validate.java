package com.RiskRmi.server;

import com.RiskRmi.enuns.FasesJogo;
import com.RiskRmi.exceptions.InvalidActionException;
import com.RiskRmi.model.Jogador;
import com.RiskRmi.model.Territorio;

import java.util.List;
import java.util.Stack;

public class Validate {

    public Validate() {
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
}
