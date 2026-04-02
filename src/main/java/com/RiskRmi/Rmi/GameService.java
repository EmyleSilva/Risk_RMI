package com.RiskRmi.Rmi;

import com.RiskRmi.exceptions.InvalidActionException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameService extends Remote {

    // Métodos de ações

    /**
     * Registra um novo jogador.
     *
     * Quando a quantidade minima de jogadores é antigida, inicia o jogo automaticamente.
     *
     * TODO: Possibilitar inicio quando os jogadores confirmam que todos entraram (respeitando as quantidades mínimas e máxima)
     *
     * @param nome O nome do jogador.
     * @return Um inteiro identificador, gerado para o jogador registrado.
     * */
    int registrarJogador(String nome) throws RemoteException, InvalidActionException;

    // Métodos de Validação

    // Métodos de Verificação de estado e status
    /**
     * Verifica se o jogo está em fase de aguardar jogadores (antes de iniciar).
     * @return true quando está aguardando, false quando o jogo já foi iniciado.
     * */
    Boolean aguardandoJogadores() throws RemoteException;
}
