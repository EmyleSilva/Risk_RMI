package com.RiskRmi.server;

import com.RiskRmi.enums.FasesJogo;
import com.RiskRmi.enums.Territorios;
import com.RiskRmi.exceptions.InvalidActionException;
import com.RiskRmi.model.Jogador;
import com.RiskRmi.model.Territorio;
import com.RiskRmi.model.Tropa;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        Territorios destinoNome = destino.getNomeEnum();

        for (Territorios t : origem.getVizinhos()) {
            if (t == destinoNome) {
                encontrouVizinho = true;
                break;
            }
        }

        if (!encontrouVizinho) {
            throw new InvalidActionException(origem.getNome() +" e " + destinoNome.getNome() + " não são vizinhos!");
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

    public void validarPassarVezInicial(Jogador jogador, FasesJogo faseAtual) {
        if(faseAtual == FasesJogo.POSICIONAMETO_INICAL && jogador.getTropasDisponiveis() > 0) {
            throw new InvalidActionException("Você deve posicionar todas as suas tropas iniciais!");
        }
    }

    public void validarPassarFaseInicial(Jogador jogadorAtual, int idUltimoJogador, FasesJogo faseAtual) {
        if (faseAtual == FasesJogo.POSICIONAMETO_INICAL && ((jogadorAtual.getId() != idUltimoJogador) || jogadorAtual.getTropasDisponiveis() != 0)) {
            throw new InvalidActionException("Você deve posicionar todas as suas tropas iniciais!");
        }
    }

    public void validarTerritorio(String nomeTerritorio, Map<String, Territorio> territorios) {
        boolean encontrouTerritorio = false;

        for (String nomeT : territorios.keySet()) {
            if (Objects.equals(nomeT, nomeTerritorio)) {
                encontrouTerritorio = true;
                break;
            }
        }

        if (!encontrouTerritorio) {
            throw new InvalidActionException("O território " + nomeTerritorio + " não existe.");
        }

    }
}
