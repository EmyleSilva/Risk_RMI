package com.RiskRmi.client;

import com.RiskRmi.Rmi.ClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {

    Scanner input = new Scanner(System.in);

    protected ClientCallbackImpl() throws RemoteException {
        super();
    }

    @Override
    public void onGameEvent(String mensagem) throws RemoteException {
        System.out.println(mensagem);
    }
}
