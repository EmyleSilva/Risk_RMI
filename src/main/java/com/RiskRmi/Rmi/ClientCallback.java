package com.RiskRmi.Rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {

    /**
     * Função de callback para exibir mensagens do servidor quando eventos específicos são disparados (
     * inicio de jogo, ocorreu um ataque, etc).
     *
     * @param mensagem Mensagem enviada pelo servidor.
     * */
    void onGameEvent(String mensagem) throws RemoteException;

    void onEndGame(String mensagem) throws RemoteException;

    void onStartGame(int jogadorId) throws RemoteException;
}
