package com.RiskRmi.server;

import com.RiskRmi.Rmi.GameService;
import com.RiskRmi.exceptions.InvalidActionException;
import com.RiskRmi.model.Jogador;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class GameServiceImpl extends UnicastRemoteObject implements GameService {

    private GameManager manager = new GameManager(30);

    protected GameServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public int registrarJogador(String nome) throws RemoteException, InvalidActionException {
        Jogador jogador = new Jogador(nome);
        manager.getJogadores().add(jogador);
        jogador.setId(manager.getJogadores().indexOf(jogador)+1);
        System.out.println("Jogador Conectado: " + nome);

        manager.verificarInicioJogo();
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
