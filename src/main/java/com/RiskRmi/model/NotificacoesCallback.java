package com.RiskRmi.model;

import com.RiskRmi.Rmi.ClientCallback;
import com.RiskRmi.enuns.FasesJogo;

import java.rmi.RemoteException;
import java.util.List;

public class NotificacoesCallback {

    /**
     * Envia uma mensagem de callback para cada um dos clientes(listeners) registrados no jogo.
     *
     * @param clientes A lista de clientes registrados.
     * @param mensagem A mensagem de callback do servidor.
     * */
    public void callback(List<ClientCallback> clientes, String mensagem) {
        for (ClientCallback c : clientes) {
            try {
                c.onGameEvent(mensagem);
            } catch (RemoteException e) {
                System.out.println("Erro: " + e.getMessage() + " ao notificar cliente.");
                clientes.remove(c);
                System.out.println("Cliente removido do servidor.\n");
            }
        }
    }

    /**
     * @return Mensagem de aviso: aguardando jogadores.
     * */
    public String aguardandoJogadores() {
        return """ 
        ======================================================================
        =                                                                    =
        =            AGUARDANDO JOGADOR(ES) PARA INICIAR A PARTIDA           =
        =                                                                    =
        ======================================================================
        """;
    }

    /**
     * @return Mensagem de aviso: jogo iniciado.
     * */
    public String jogoIniciado() {
        return """ 
        ======================================================================
        =                                                                    =
        =                          JOGO INICIADO!                            =
        =                                                                    =
        ======================================================================
        """;
    }

    /**
     * @param nomeJogadorAtual Nome do jogador atual para informar de quem é o turno.
     * @return Mensagem de aviso: posicionamento atual.
     * */
    public String posicionamentoInicial(String nomeJogadorAtual) {
        return """ 
                ======================================================================
                                                                                    
                                   FASE DE POSICIONAMENTO INICIAL                                                      
                                                                                    
                                          Jogador Atual > %s                                         
                ======================================================================
                """.formatted(nomeJogadorAtual);
    }

    /**
     * @param nome Nome do jogador atual para informar quem realizou a ação acionada.
     * @param nomeTerritorio O território onde a tropa foi adicionada.
     * @param totalTropas A quantidade de tropas adicionadas.
     * @return Mensagem de aviso: nova ação - adição de tropas em um território.
     * */
    public String tropasAdicionadas(String nome, String nomeTerritorio, Integer totalTropas) {
        return """ 
                ======================================================================                                                                   
                                         NOVA AÇÃO REALIZADA!                         
                                  O Jogador %s adicionou tropas em:                  
                                           %s | Total Tropas: %d                     
                ======================================================================
                """.formatted(nome, nomeTerritorio, totalTropas);
    }

    /**
     * @param nome Nome do jogador atual.
     * @param faseAtual A nova fase iniciada.
     * @return Mensagem de aviso: nova fase iniciada.
     * */
    public String novaFase(String nome, FasesJogo faseAtual) {
        return """ 
                ======================================================================
                                        FASE DE %s                                        
                                     Jogador Atual > %s                          
                ======================================================================
                """.formatted(faseAtual.getDescricao(), nome);
    }
}
