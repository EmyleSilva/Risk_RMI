package com.RiskRmi.client;

import com.RiskRmi.Rmi.ClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {

    private UserCLI cliente;

    protected ClientCallbackImpl(UserCLI cliente) throws RemoteException {
        super();
        this.cliente = cliente;
    }

    @Override
    public void onGameEvent(String mensagem) throws RemoteException {
        System.out.println(mensagem);
    }

    @Override
    public void onEndGame(String mensagem) throws RemoteException {
        cliente.encerrarJogo(mensagem);
    }
}
