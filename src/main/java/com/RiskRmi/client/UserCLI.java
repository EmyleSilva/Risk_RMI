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

    public void aguardandoJogador() {
        System.out.println("======================================================================");
        System.out.println("=                                                                    =");
        System.out.println("=                                                                    =");
        System.out.println("=            AGUARDANDO JOGADOR(ES) PARA INICIAR A PARTIDA           =");
        System.out.println("=                                                                    =");
        System.out.println("=                                                                    =");
        System.out.println("======================================================================");
    }

//    public void controladorJogo() throws RemoteException{
//        while (true) {
//            try {
//                if (risk.jogoIniciado()) {
//                    exibirEstadoDoJogo();
//                }
//
//                exibirMenuPrincipal();
//                switch (this.opcaoMenu) {
//                    case 1:
//                        risk.verificarTurno(jogadorId);
//                        opcaoPosicionarTropas();
//                        break;
//                    case 2:
//                        risk.verificarTurno(jogadorId);
//                        opcaoAtacar();
//                        break;
//                    case 3:
//                        risk.verificarTurno(jogadorId);
//                        opcaoMovimentarTropas();
//                        break;
//                    case 4:
//                        exibirEstadoDoJogo();
//                        break;
//                    default:
//                        System.out.println("Opção Inválida!");
//                }
//            }catch (InvalidActionException e) {
//                System.out.println(e.getMessage());
//            }
//        }
//    }
//
//    public void exibirEstadoDoJogo() throws RemoteException{
//        EstadoJogo estado = risk.getEstado();
//        estado.estadoAtual();
//    }
//
//    public void exibirMenuPrincipal() {
//        System.out.println("==================== MENU ====================== ");
//        System.out.println("Escolha uma opção abaixo: ");
//        System.out.println("""
//                        1. Posicionar Tropas \n
//                        2. Atacar \n
//                        3. Movimentar Tropas \n
//                        4. Mostrar Estado do Jogo \n
//                        """);
//        this.opcaoMenu = input.nextInt();
//    }
//
//    public void opcaoPosicionarTropas() throws RemoteException {
//
//        int quantidadeTropas; int index = 1;
//        Territorio territorioEscolhido;
//        List<Territorio> territorios = risk.buscarTerritoriosJogador(this.jogadorId);
//
//        for (Territorio t : territorios) {
//            System.out.println("(" + index++ + "): " + t.getTerritorio());
//        }
//        System.out.println("Selecione um território: ");
//        territorioEscolhido = territorios.get(input.nextInt()-1);
//
//        System.out.println("Total de Tropas Disponiveis: " + risk.quantidadesTropasDisponiveis(this.jogadorId));
//        System.out.println("Quantas tropas deseja posicionar? ");
//        quantidadeTropas = input.nextInt();
//
//        risk.posicionarTropasIniciais(jogadorId, territorioEscolhido.getTerritorio(), quantidadeTropas);
//    }
//
//    public void opcaoAtacar() {
//        System.out.println("TODO!");
//    }
//
//    public void opcaoMovimentarTropas() {
//        System.out.println("TODO!");
//    }
}
