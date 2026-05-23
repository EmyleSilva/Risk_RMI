package com.RiskRmi.client;

import com.RiskRmi.Rmi.ClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {

    private UserCLI cliente;

    protected ClientCallbackImpl(UserCLI cliente, int port) throws RemoteException {
        super();
        this.cliente = cliente;
    }

    @Override
    public void onGameEvent(String mensagem) throws RemoteException {
        System.out.println(mensagem);
    }

    /**
     * Inicia uma nova Thread para que o encerramento do cliente seja realizado sem criar um deadlock.
     * Sem a Thread, ao remover um jogador que perdeu, o retorno não é realizado, fazendo o servidor
     * e os demais clientes ficarem em espera infinita (bloqueados).
     * Com a Thread, a chamada encerrarJogo é feita em paralelo com o retorno imediato do método para o
     * servidor, de modo que o deadlock é evitado.
     * */
    @Override
    public void onEndGame(String mensagem) throws RemoteException {
        new Thread(() -> {
            cliente.encerrarJogo(mensagem);
        }).start();
    }

    @Override
    public void onStartGame(int jogadorId) throws RemoteException {
        cliente.setJogadorId(jogadorId);
        cliente.iniciarJogo();
    }
}
