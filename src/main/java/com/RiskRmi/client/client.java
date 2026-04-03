package com.RiskRmi.client;

import com.RiskRmi.Rmi.ClientCallback;
import com.RiskRmi.Rmi.GameService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class client {

    static Scanner input = new Scanner(System.in);

    static String nomeJogador = "";
    static String name = "rmi://localhost:1099/risk";
    static GameService risk = null;
    static int jogadorId;
    static UserCLI user;

    public static void main(String[] args) {
        try {
            System.out.println("============================ RISK GAME ============================");
            System.out.println("Bem Vindo ao Risk! Digite um username para se juntar a partida: ");
            nomeJogador = input.nextLine();

            risk = (GameService) Naming.lookup(name);

            /** Cria um objeto de callback e o envia para registro no servidor */
            ClientCallback callback = new ClientCallbackImpl();
            risk.registrarCliente(callback);

            /** Registra um novo jogador */
            jogadorId = risk.registrarJogador(nomeJogador);
            System.out.println("Bem Vindo ao Risk," + nomeJogador + "! Seu id é: " + jogadorId);

            user = new UserCLI(risk, jogadorId);

            Thread.sleep(3000);

            user.controladorJogo();

        }catch (RemoteException | NotBoundException | MalformedURLException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
