package com.RiskRmi.server;

import com.RiskRmi.Rmi.ClientCallback;
import com.RiskRmi.Rmi.GameService;
import com.RiskRmi.exceptions.InvalidActionException;
import com.RiskRmi.model.Jogador;
import com.RiskRmi.model.Territorio;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class GameServiceImpl extends UnicastRemoteObject implements GameService  {

    private final GameManager manager;
    private final List<ClientCallback> clientes;

    protected GameServiceImpl() throws RemoteException {
        super();
        clientes = new ArrayList<>();
        manager = new GameManager(30, clientes);
    }

    @Override
    public int registrarJogador(String nome, ClientCallback cliente) throws RemoteException, InvalidActionException {
        Jogador jogador = new Jogador(nome, cliente);
        manager.getJogadores().add(jogador);
        manager.registrarCliente(cliente);

        jogador.setId(manager.getJogadores().indexOf(jogador)+1);
        System.out.println("Jogador Conectado: " + nome);

        manager.verificarInicioJogo();
        return jogador.getId();
    }

    @Override
    public void posicionamentoInicialDeTropas(int jogadorId, String nomeTerritorio, int quantidadeTropas) throws RemoteException, InvalidActionException {
        manager.posicionamentoInicial(jogadorId, nomeTerritorio, quantidadeTropas);
    }

    @Override
    public void passarVez(int jogadorId) throws RemoteException, InvalidActionException {
        manager.passarVez(jogadorId);
    }

    @Override
    public List<String> buscarTerritoriosTropasJogador(int jogadorId) throws RemoteException, InvalidActionException {
        return manager.buscarTerritoriosTropasJogador(jogadorId);
    }

    @Override
    public List<String> buscarTerritoriosJogador(int jogadorId) throws RemoteException, InvalidActionException {
        return manager.buscarTerritoriosJogador(jogadorId);
    }

    @Override
    public Integer buscarTotalTropasTerritorio(String nomeTerritorio) throws RemoteException, InvalidActionException {
        return manager.totalTropasTerritorio(nomeTerritorio);
    }

    @Override
    public Integer quantidadeTropasDisponiveis(int jogadorId) throws RemoteException, InvalidActionException {
        return manager.buscarTropasDisponiveisJogador(jogadorId);
    }

    @Override
    public void atacar(int jogadorId, String origem, String destino) throws RemoteException, InvalidActionException {
        manager.atacar(jogadorId, origem, destino);
    }

    @Override
    public void passarFase(int jogadorId) throws RemoteException, InvalidActionException {
        manager.proximaFase(jogadorId);
    }

    @Override
    public List<Territorio> buscarTerritorios() throws RemoteException {
        return manager.buscarTerritorios();
    }

    @Override
    public void movimentar(int jogadorId, String origem, String destino, Integer quantidadeTropas) throws RemoteException, InvalidActionException {
        manager.movimentarTropas(jogadorId, origem, destino, quantidadeTropas);
    }

    @Override
    public List<String> buscarCartasJogador(int jogadorId) throws RemoteException, InvalidActionException {
        return manager.buscarCartasJogador(jogadorId);
    }

    @Override
    public String trocarCartas(int jogadorId) throws RemoteException, InvalidActionException {
        return manager.calcularBonusCartas(jogadorId);
    }

    @Override
    public void posicionarTropas(int jogadorId, String territorio, Integer quantidadeTropas) throws RemoteException, InvalidActionException {
        manager.posicionarTropas(jogadorId, territorio, quantidadeTropas);
    }

    @Override
    public String buscarEstadoJogo() throws RemoteException {
        return manager.exibirEstadoJogo();
    }

    public static void main(String[] args) {
        try {
//            System.setProperty("java.rmi.server.hostname", "192.168.15.7");
            LocateRegistry.createRegistry(1099);

            GameServiceImpl risk = new GameServiceImpl();
            String name = "rmi://localhost/risk";
            Naming.rebind(name, risk);
            System.out.println("Servidor Iniciado.......");

        } catch (RemoteException | MalformedURLException | InvalidActionException e) {

            System.out.println(e.getMessage());
        }
    }
}
