package com.RiskRmi.client;

import com.RiskRmi.Rmi.ClientCallback;
import com.RiskRmi.Rmi.GameService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

    static Scanner input = new Scanner(System.in);

    static String nomeJogador = "";
    static GameService risk = null;
    static int jogadorId;
    static UserCLI user;
    static final String IP_SERVIDOR = "127.0.0.1";

    public static void main(String[] args) {
        try {
            /**
             * O ip do cliente, que deve ser passado como argumento.
             * */
            String ip_client = args[0];

            /**
             * Configura o nome do servidor RMI do cliente.
             * Dessa forma, o servidor conseguirá localizar o cliente
             * para chamadas de callback.
             * */
            System.setProperty(
              "java.rmi.server.hostname",
              ip_client
            );

            System.out.println("============================ RISK GAME - ONE PIECE ============================");
            System.out.println("Bem Vindo ao Risk! Digite um username para se juntar a partida: ");
            nomeJogador = input.nextLine();

            /** Se conecta ao registry do servidor * */
            Registry registry = LocateRegistry.getRegistry(
                    IP_SERVIDOR,
                    1099
            );

            /** Busca o stub remoto * */
            risk = (GameService) registry.lookup("risk");
            user = new UserCLI(risk);

            int port = risk.solicitarPortaCallback();

            /** Cria um objeto de callback*/
            ClientCallback callback = new ClientCallbackImpl(user, port);

            /** Registra um novo jogador juntamente com o objeto de callback para o servidor*/
            jogadorId = risk.registrarJogador(nomeJogador, callback);

        }catch (RemoteException | NotBoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
