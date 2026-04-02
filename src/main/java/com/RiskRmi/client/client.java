package com.RiskRmi.client;

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
            jogadorId = risk.registrarJogador(nomeJogador);
            System.out.println("Bem Vindo ao Risk," + nomeJogador + "! Seu id é: " + jogadorId);

            user = new UserCLI(risk, jogadorId);

            if (risk.aguardandoJogadores()) user.aguardandoJogador();
            while(risk.aguardandoJogadores()) {}

//            user.controladorJogo();

        }catch (RemoteException | NotBoundException | MalformedURLException e) {
            System.out.println(e.getMessage());
        }
    }
}
