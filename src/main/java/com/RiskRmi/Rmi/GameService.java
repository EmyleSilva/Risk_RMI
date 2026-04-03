package com.RiskRmi.Rmi;

import com.RiskRmi.exceptions.InvalidActionException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameService extends Remote {
    /**
     * Registro de cliente para callbacks do servidor.
     *
     * @param cliente Novo cliente a ser registrado.
     * */
    void registrarCliente(ClientCallback cliente) throws RemoteException;

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

    /**
     * Realiza um posicionamento de tropas em um território, usada apenas na fase de posicionamento inical.
     * @param jogadorId Id do jogador que realizou a requisição.
     * @param nomeTerritorio Territorio em que se deseja adicionar tropas.
     * @param quantidadeTropas A quantidade de tropas que deve ser adicionada.
     * */
    void posicionamentoInicialDeTropas(int jogadorId, String nomeTerritorio, int quantidadeTropas) throws RemoteException, InvalidActionException;

    /**
     * Passa a vez do jogador atual para o próximo jogador, criando automáticamente novos turnos para o jogador.
     * @param jogadorId Id do jogador que realizou a requisição.
     * */
    void passarVez(int jogadorId) throws RemoteException, InvalidActionException;

    // Métodos de Verificação de estado e status
    /**
     * Busca os nomes de todos os territorios do jogador,
     * juntamente com a quantidade total de cada tipo tropa presente no territorio.
     *
     * @param jogadorId Id do jogador que realizou a requisição.
     * @return Uma lista com todos os nomes e tropas dos territorios.
     * */
    List<String> buscarTerritoriosTropasJogador(int jogadorId) throws RemoteException, InvalidActionException;

    /**
     * Busca os nomes de todos os territórios de um jogador.
     * @param jogadorId Jogador que realizou a requisição.
     * @return Uma lista com todos os nomes dos territorios.
     * */
    List<String> buscarTerritoriosJogador(int jogadorId) throws RemoteException, InvalidActionException;

    /**
     * Retorna a quantidade total de tropas (cavalaria + infantaria) do territorio
     * @param nomeTerritorio O territorio para busca do total.
     * @return A quantidade total de tropas.
     * */
    Integer buscarTotalTropasTerritorio(String nomeTerritorio) throws RemoteException, InvalidActionException;

    /**
     * Busca a quantidade total de tropas que o jogador ainda tem disponivel para posicionamento inicial.
     * @param jogadorId Id do Jogador que realizou a requisição.
     * @return A quantidade de tropas disponiveis.
     * */
    Integer quantidadeTropasDisponiveis(int jogadorId) throws RemoteException, InvalidActionException;
}
