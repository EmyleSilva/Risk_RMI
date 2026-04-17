package com.RiskRmi.client;

import com.RiskRmi.Rmi.GameService;
import com.RiskRmi.exceptions.InvalidActionException;
import com.RiskRmi.model.Territorio;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

public class UserCLI implements Runnable{

    private Scanner input = new Scanner(System.in);
    private int jogadorId;
    private int opcaoMenu;
    private GameService risk;
    private boolean jogoAtivo = true;
    private Thread clienteThread;

    public UserCLI(GameService risk) {
        this.risk = risk;
    }

    public void iniciarJogo() {
        clienteThread = new Thread(this);
        clienteThread.start();
    }

    public void encerrarJogo(String mensagem) {
        System.out.println(mensagem);

        jogoAtivo = false;

        if (clienteThread != null) {
            clienteThread.interrupt();
        }

        input.close();
    }

    public void setJogadorId(int jogadorId) {
        this.jogadorId = jogadorId;
    }

    @Override
    public void run() {
        while (jogoAtivo) {
            try {
                exibirMenuPrincipal();
                switch (this.opcaoMenu) {
                    case 1:
                        opcaoPosicionarTropasIniciais();
                        break;
                    case 2:
                        opcaoPosicionarTropas();
                        break;
                    case 3:
                        opcaoAtacar();
                        break;
                    case 4:
                        opcaoMovimentarTropas();
                        break;
                    case 0:
                        risk.passarVez(this.jogadorId);
                        break;
                    default:
                        System.out.println("Opção Inválida!");
                }
            }catch (InvalidActionException | RemoteException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void exibirMenuPrincipal() {
        System.out.println("==================== MENU ====================== ");
        System.out.println("Escolha uma opção abaixo: ");
        System.out.println("""
                        1. Posicionar Tropas Iniciais \n
                        2. Posicionar Tropas \n
                        3. Atacar \n
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

            System.out.println("(1) - CONTINUAR ATAQUE\n(0) - IR PARA PRÓXIMA FASE\n");
            continuarAtaque = input.nextInt();
            input.nextLine(); //Limpar o buffer.
        }

        if (jogoAtivo) risk.passarFase(jogadorId);
    }

    public void exibirTerritorios() throws RemoteException{
        List<Territorio> territorios = risk.buscarTerritorios();

        System.out.println("**************************** TERRITÓRIOS ****************************");
        for (Territorio t : territorios) {
            System.out.println(t);
        }
        System.out.println("*********************************************************************");

    }

    public void opcaoPosicionarTropas() throws  RemoteException {
        List<String> cartas = risk.buscarCartasJogador(jogadorId);
        int quantidadeTropas = 0;
        String territorio;

        System.out.println("**************************** CARTAS ****************************");
        if (!cartas.isEmpty()) {
            for (String c : cartas) {
                System.out.println(c);
            }
        }else {
            System.out.println("Deck de Cartas Vazio!");
        }

        System.out.println("\nVocê pode trocar 3 cartas iguais por bonificação");
        System.out.println("*********************************************************************");

        int escolha = 0;
        System.out.println("\nDeseja trocar cartas por bonificação?\n(0) Não\n(1) Sim");
        escolha = input.nextInt();
        input.nextLine();

        if (escolha == 1) {
            System.out.println(risk.trocarCartas(jogadorId));
        }

        do {
            exibirTerritorios();
            System.out.println("Você possui " + risk.quantidadeTropasDisponiveis(jogadorId) + " tropas disponíveis");
            System.out.println("Digite o nome do território ");
            territorio = input.nextLine();

            System.out.println("Digite quantas tropas deseja posicionar no território: ");
            quantidadeTropas = input.nextInt();
            input.nextLine();

            risk.posicionarTropas(jogadorId, territorio, (Integer) quantidadeTropas);

            System.out.println("(1) - CONTINUAR POSICIONAMENTO\n(0) - IR PARA PRÓXIMA FASE");
            escolha = input.nextInt();;
            input.nextLine();
        }while (escolha == 1);

        risk.passarFase(jogadorId);
    }

    public void opcaoMovimentarTropas() throws RemoteException{
        exibirTerritorios();

        String origem, destino;
        int quantidadeTropas;

        System.out.println("Digite o nome do território de origem das tropas: ");
        origem = input.nextLine();
        System.out.println("Digite o nome do território de destino (fortificar): ");
        destino = input.nextLine();
        System.out.println("Digite quantas tropas deseja mover: ");
        quantidadeTropas = input.nextInt();

        risk.movimentar(jogadorId, origem, destino, (Integer) quantidadeTropas);
    }
}
