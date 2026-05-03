package com.RiskRmi.client;

import com.RiskRmi.Rmi.GameService;
import com.RiskRmi.exceptions.InvalidActionException;

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
                    case 5:
                        risk.passarFase(this.jogadorId);
                        break;
                    case 6:
                        risk.passarVez(this.jogadorId);
                        break;
                    case 7:
                        System.out.println(risk.buscarEstadoJogo());
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
                        5. Próxima Fase  \n
                        6. Passar a vez \n
                        7. Mostrar Estado do Jogo \n
                        """);
        this.opcaoMenu = input.nextInt();
        input.nextLine(); //Limpar o buffer.
    }

    public void opcaoPosicionarTropasIniciais() throws RemoteException, InvalidActionException {
        try {
            int quantidadeTropas; int index = 1; int continuarAtaque = 0;
            String territorioEscolhido;
            List<String> territorios = risk.buscarTerritoriosJogador(this.jogadorId);

            do {
                for (String t : territorios) {
                    System.out.println("(" + index++ + "): " + t + " >>>>>> Total Tropas: " + risk.buscarTotalTropasTerritorio(t));
                }
                System.out.println("Selecione um território: ");
                territorioEscolhido = territorios.get(input.nextInt() - 1);
                input.nextLine(); //Limpar o buffer.

                System.out.println("Total de Tropas Disponiveis: " + risk.quantidadeTropasDisponiveis(this.jogadorId));
                System.out.println("Quantas tropas deseja posicionar? ");
                quantidadeTropas = input.nextInt();
                input.nextLine(); //Limpar o buffer.

                risk.posicionamentoInicialDeTropas(jogadorId, territorioEscolhido, quantidadeTropas);

                System.out.println("(1) CONTINUAR POSICIONAMENTO INICIAL\n(0) VOLTAR PARA O MENU");
                continuarAtaque = input.nextInt();
                input.nextLine(); //Limpar o buffer
                index = 1;
            }while (continuarAtaque == 1);
        }catch (IndexOutOfBoundsException e) {
            System.out.println("Opção Inválida, Selecione 'Posicionar Tropas Iniciais' e tente novamente!\n");
        }
    }

    public void opcaoAtacar() throws RemoteException, InvalidActionException {
        try {
            int continuarAtaque = 1, index = 1;

            String origem, destino;

            while (continuarAtaque != 0) {
                List<String> territorios = risk.buscarTerritoriosJogador(jogadorId);
                List<String> territoriosInimigos = risk.buscarTerritoriosInimigos(jogadorId);

                System.out.println("**************************** SEUS TERRITÓRIOS ****************************");
                for (String t : territorios) {
                    System.out.println("(" + index++ + "): " + t + " >>>>>> Total Tropas: " + risk.buscarTotalTropasTerritorio(t));
                }
                System.out.println("Selecione um território: ");
                origem = territorios.get(input.nextInt() - 1);
                input.nextLine(); //Limpar Buffer
                index = 1;

                System.out.println("**************************** TERRITÓRIOS INIMIGOS ****************************");
                for (String t : territoriosInimigos) {
                    System.out.println("(" + index++ + "): " + t + " >>>>>> Total Tropas: " + risk.buscarTotalTropasTerritorio(t));
                }
                destino = territoriosInimigos.get(input.nextInt() - 1);
                input.nextLine(); //Limpar Buffer
                index = 1;

                risk.atacar(jogadorId, origem, destino);

                System.out.println("(1) - CONTINUAR ATAQUE\n(0) - IR PARA PRÓXIMA FASE\n");
                continuarAtaque = input.nextInt();
                input.nextLine(); //Limpar o buffer.
            }

            if (jogoAtivo) risk.passarFase(jogadorId);
        }catch (IndexOutOfBoundsException e) {
            System.out.println("Opção Inválida! Selecione Atacar e tente novamente!\n");
        }
    }

    public void opcaoPosicionarTropas() throws  RemoteException {
        try {
            List<String> cartas = risk.buscarCartasJogador(jogadorId);
            List<String> territorios = risk.buscarTerritoriosJogador(jogadorId);

            int quantidadeTropas = 0;
            int index = 1;
            String territorio;

            System.out.println("**************************** CARTAS ****************************");
            if (!cartas.isEmpty()) {
                for (String c : cartas) {
                    System.out.println(c);
                }
            } else {
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
                System.out.println("Selecione o território: ");
                for (String t : territorios) {
                    System.out.println("(" + index++ + "): " + t + " >>>>>> Total Tropas: " + risk.buscarTotalTropasTerritorio(t));
                }
                territorio = territorios.get(input.nextInt() - 1);
                System.out.println("Você possui " + risk.quantidadeTropasDisponiveis(jogadorId) + " tropas disponíveis");

                System.out.println("Digite quantas tropas deseja posicionar no território: ");
                quantidadeTropas = input.nextInt();
                input.nextLine();

                risk.posicionarTropas(jogadorId, territorio, (Integer) quantidadeTropas);

                System.out.println("(1) - CONTINUAR POSICIONAMENTO\n(0) - IR PARA PRÓXIMA FASE");
                escolha = input.nextInt();
                ;
                input.nextLine();
                index = 1;
            } while (escolha == 1);

            if (jogoAtivo) risk.passarFase(jogadorId);
        }catch (IndexOutOfBoundsException e) {
            System.out.println("Opção Inválida! Selecione 'Posicionar Tropas' e tente novamente!\n");
        }
    }

    public void opcaoMovimentarTropas() throws RemoteException{
        try {
            List<String> territorios = risk.buscarTerritoriosJogador(jogadorId);
            String origem, destino;
            int quantidadeTropas, index = 1;

            System.out.println("********************************* Territórios *********************************");
            for (String t : territorios) {
                System.out.println("(" + index++ + "): " + t + " >>>>>> Total Tropas: " + risk.buscarTotalTropasTerritorio(t));
            }
            System.out.println("Escolha o Território de Origem:");
            origem = territorios.get(input.nextInt() - 1);
            input.nextLine(); //Limpar Buffer
            System.out.println("Escolha o território de destino (fortificar): ");
            destino = territorios.get(input.nextInt() - 1);
            input.nextLine(); //Limpar Buffer
            System.out.println("Digite quantas tropas deseja mover: ");
            quantidadeTropas = input.nextInt();
            input.nextLine(); //Limpar Buffer

            risk.movimentar(jogadorId, origem, destino, (Integer) quantidadeTropas);
        }catch (IndexOutOfBoundsException e) {
            System.out.println("Opção Inválida, selecione 'Movimentar Tropas' e tente novamente!\n");
        }
    }
}
