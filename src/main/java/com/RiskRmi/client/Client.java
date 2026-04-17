package com.RiskRmi.client;

import com.RiskRmi.Rmi.ClientCallback;
import com.RiskRmi.Rmi.GameService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {

    static Scanner input = new Scanner(System.in);

    static String nomeJogador = "";
    static String name = "rmi://localhost/risk";
    static GameService risk = null;
    static int jogadorId;
    static UserCLI user;

    public static void main(String[] args) {
        try {
            System.out.println("============================ RISK GAME ============================");
            System.out.println("Bem Vindo ao Risk! Digite um username para se juntar a partida: ");
            nomeJogador = input.nextLine();

            risk = (GameService) Naming.lookup(name);

            user = new UserCLI(risk);

            /** Cria um objeto de callback*/
            ClientCallback callback = new ClientCallbackImpl();

            /** Registra um novo jogador juntamente com o objeto de callback para o servidor*/
            jogadorId = risk.registrarJogador(nomeJogador, callback);
            System.out.println("Bem Vindo ao Risk," + nomeJogador + "! Seu id é: " + jogadorId);

            user.setJogadorId(jogadorId);

            Thread.sleep(3000);

            user.controladorJogo();

        }catch (RemoteException | NotBoundException | MalformedURLException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
