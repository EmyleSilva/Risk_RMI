package com.RiskRmi.Rmi;

import com.RiskRmi.exceptions.InvalidActionException;
import com.RiskRmi.model.Carta;
import com.RiskRmi.model.Territorio;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameService extends Remote {
    // Métodos de ações

    /**
     * Registra um novo jogador.
     *
     * Quando a quantidade minima de jogadores é antigida, inicia o jogo automaticamente.
     *
     * @param nome O nome do jogador.
     * @return Um inteiro identificador, gerado para o jogador registrado.
     * */
    int registrarJogador(String nome, ClientCallback cliente) throws RemoteException, InvalidActionException;

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

    /**
     * Método que executa um ataque.
     *
     * @param jogadorId O jogador que deseja atacar.
     * @param origem O território de origem do ataque.
     * @param destino O território que será atacado.
     * */
    void atacar(int jogadorId, String origem, String destino) throws RemoteException, InvalidActionException;

    /**
     * Muda a fase do jogo para a próxima da pilha. Se for a última fase do turno de um jogador,
     * passa para o turno do próximo jogador da fila.
     *
     * @param jogadorId O Id do jogador que está passando a fase.
     * */
    void passarFase(int jogadorId) throws RemoteException, InvalidActionException;

    /**
     * Realiza uma busca por todos os territórios do jogo.
     * @return Uma lista com o nome de todos os territórios.
     * */
    List<Territorio> buscarTerritorios() throws RemoteException;

    /**
     * Movimenta as tropas de um jogador após a fase de ataque.
     * Os territórios de origem e destino devem ambos pertencer ao jogador que está realizando a
     * movimentação.
     *
     * @param jogadorId O id do jogador que solicita a movimentação de tropas.
     * @param origem O território de origem.
     * @param destino O território de destino.
     * */
    void movimentar(int jogadorId, String origem, String destino, Integer quantidadeTropas) throws RemoteException, InvalidActionException;

    /**
     * Busca o nome de todas as cartas que o jogador possui.
     * @param jogadorId O id do jogador.
     * @return Uma lista com os nomes de todas as cartas.
     * */
    List<String> buscarCartasJogador(int jogadorId) throws RemoteException, InvalidActionException;

    /**
     * Realiza o calculo de bonificação por troca de cartas, sempre que o jogador possui
     * cartas suficientes e atende ao critério de troca (3 cartas iguais).
     * @param jogadorId O id do jogador.
     * @return Uma mensagem indicando se houve ou não a bonificação por troca.
     * */
    String trocarCartas(int jogadorId) throws RemoteException, InvalidActionException;

    /**
     * Posiciona tropas de bonificação em um território do jogador.
     * @param jogadorId O id do jogador.
     * @param territorio O nome do território que o jogador deseja fortificar.
     * @param quantidadeTropas A quantidade de tropas que será adicionada.
     * */
    void posicionarTropas(int jogadorId, String territorio, Integer quantidadeTropas) throws RemoteException, InvalidActionException;

    /**
     * Busca as informações do estado atual do jogo.
     * Atualiza o jogador sobre os territórios, continentes, jogadores e informações gerais.
     * @return Uma string com todas as informações do estado do jogo.
     * */
    String buscarEstadoJogo() throws RemoteException;
}
