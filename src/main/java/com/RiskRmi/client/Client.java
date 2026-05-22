package com.RiskRmi.client;

import com.RiskRmi.Rmi.ClientCallback;
import com.RiskRmi.Rmi.GameService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

    static Scanner input = new Scanner(System.in);

    static String nomeJogador = "";
//    static String name = "rmi://192.168.15.10:1099/risk";
    static GameService risk = null;
    static int jogadorId;
    static UserCLI user;

    public static void main(String[] args) {
        try {
            String ip_client = args[0];

            System.setProperty(
              "java.rmi.server.hostname",
              ip_client
            );

            System.out.println("============================ RISK GAME - ONE PIECE ============================");
            System.out.println("Bem Vindo ao Risk! Digite um username para se juntar a partida: ");
            nomeJogador = input.nextLine();

            Registry registry = LocateRegistry.getRegistry(
                    "192.168.15.10",
                    1099
            );

//            risk = (GameService) Naming.lookup(name);
            risk = (GameService) registry.lookup("risk");
            user = new UserCLI(risk);

            /** Cria um objeto de callback*/
            ClientCallback callback = new ClientCallbackImpl(user);

            /** Registra um novo jogador juntamente com o objeto de callback para o servidor*/
            jogadorId = risk.registrarJogador(nomeJogador, callback);

        }catch (RemoteException | NotBoundException /*| MalformedURLException*/ e) {
            System.out.println(e.getMessage());
        }
    }
}
