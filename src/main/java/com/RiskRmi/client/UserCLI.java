package com.RiskRmi.client;

import com.RiskRmi.Rmi.GameService;
import com.RiskRmi.exceptions.InvalidActionException;
import com.RiskRmi.model.Territorio;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

public class UserCLI {

    private Scanner input = new Scanner(System.in);
    private String nomeJogador;
    private int jogadorId;
    private int opcaoMenu;
    private GameService risk;

    public UserCLI(GameService risk, int jogadorId) {
        this.risk = risk;
        this.jogadorId = jogadorId;
    }

    public void controladorJogo() throws RemoteException{
        while (true) {
            try {
                exibirMenuPrincipal();
                switch (this.opcaoMenu) {
                    case 1:
                        opcaoPosicionarTropasIniciais();
                        break;
                    case 2:
                        opcaoAtacar();
                        break;
                    case 3:
                        System.out.println("TODO");
//                        opcaoMovimentarTropas();
                        break;
                    case 4:
                        System.out.println("TODO");
//                        exibirEstadoDoJogo();
                        break;
                    case 0:
                        risk.passarVez(this.jogadorId);
                        break;
                    default:
                        System.out.println("Opção Inválida!");
                }
            }catch (InvalidActionException e) {
                System.out.println(e.getMessage());
            }
        }
    }
//
//    public void exibirEstadoDoJogo() throws RemoteException{
//        EstadoJogo estado = risk.getEstado();
//        estado.estadoAtual();
//    }
//
    public void exibirMenuPrincipal() {
        System.out.println("==================== MENU ====================== ");
        System.out.println("Escolha uma opção abaixo: ");
        System.out.println("""
                        1. Posicionar Tropas Iniciais \n
                        2. Atacar\n
                        3. Posicionar Tropas \n
                        4. Movimentar Tropas \n
                        5. Mostrar Estado do Jogo \n
                        0. Passar a Vez\n
                        """);
        this.opcaoMenu = input.nextInt();
        input.nextLine(); //Limpar o buffer.
    }

    public void opcaoPosicionarTropasIniciais() throws RemoteException, InvalidActionException {

        int quantidadeTropas; int index = 1;
        String territorioEscolhido;
        List<String> territorios = risk.buscarTerritoriosJogador(this.jogadorId);

        for (String t : territorios) {
            System.out.println("(" + index++ + "): " + t + " >>>>>> Total Tropas: " + risk.buscarTotalTropasTerritorio(t));
        }
        System.out.println("Selecione um território: ");
        territorioEscolhido = territorios.get(input.nextInt()-1);
        input.nextLine(); //Limpar o buffer.

        System.out.println("Total de Tropas Disponiveis: " + risk.quantidadeTropasDisponiveis(this.jogadorId));
        System.out.println("Quantas tropas deseja posicionar? ");
        quantidadeTropas = input.nextInt();
        input.nextLine(); //Limpar o buffer.

        risk.posicionamentoInicialDeTropas(jogadorId, territorioEscolhido, quantidadeTropas);
    }

    public void opcaoAtacar() throws RemoteException, InvalidActionException {
        int continuarAtaque = 1;
        exibirTerritorios();

        String origem, destino;

        while (continuarAtaque != 0) {
            System.out.println("Digite o nome do território de origem: ");
            origem = input.nextLine();
            System.out.println("Digite o nome do território que você quer atacar: ");
            destino = input.nextLine();

            risk.atacar(jogadorId, origem, destino);

            System.out.println("(1) - CONTINUAR ATAQUE\n (0) - IR PARA PRÓXIMA FASE\n");
            continuarAtaque = input.nextInt();
            input.nextLine(); //Limpar o buffer.
        }

        risk.passarFase(jogadorId);
    }

    public void exibirTerritorios() throws RemoteException{
        List<Territorio> territorios = risk.buscarTerritorios();

        System.out.println("**************************** TERRITÓRIOS ****************************");
        for (Territorio t : territorios) {
            System.out.println(t);
        }
        System.out.println("*********************************************************************");

    }
//
//    public void opcaoMovimentarTropas() {
//        System.out.println("TODO!");
//    }
}
