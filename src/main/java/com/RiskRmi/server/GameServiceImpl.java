package com.RiskRmi.server;

import com.RiskRmi.Rmi.ClientCallback;
import com.RiskRmi.Rmi.GameService;
import com.RiskRmi.exceptions.InvalidActionException;
import com.RiskRmi.model.Jogador;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class GameServiceImpl extends UnicastRemoteObject implements GameService  {

    private GameManager manager;
    private List<ClientCallback> clientes;

    protected GameServiceImpl() throws RemoteException {
        super();
        clientes = new ArrayList<>();
        manager = new GameManager(30, clientes);
    }

    @Override
    public void registrarCliente(ClientCallback cliente) throws RemoteException {
        manager.registrarCliente(cliente);
    }

    @Override
    public int registrarJogador(String nome) throws RemoteException, InvalidActionException {
        Jogador jogador = new Jogador(nome);
        manager.getJogadores().add(jogador);
        jogador.setId(manager.getJogadores().indexOf(jogador)+1);
        System.out.println("Jogador Conectado: " + nome);

        manager.verificarInicioJogo(clientes);
        return jogador.getId();
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);

            GameServiceImpl risk = new GameServiceImpl();
            String name = "rmi://localhost:1099/risk";
            Naming.rebind(name, risk);
            System.out.println("Servidor Iniciado.......");

        } catch (RemoteException | MalformedURLException | InvalidActionException e) {

            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
