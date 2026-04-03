package com.RiskRmi.client;

import com.RiskRmi.Rmi.ClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {

    protected ClientCallbackImpl() throws RemoteException {
        super();
    }

    @Override
    public void onGameEvent(String mensagem) throws RemoteException {
        System.out.println(mensagem);
    }
}
