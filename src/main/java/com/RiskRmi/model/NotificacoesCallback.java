package com.RiskRmi.model;

import com.RiskRmi.Rmi.ClientCallback;
import com.RiskRmi.enums.FasesJogo;

import java.awt.*;
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

    public void callback(List<ClientCallback> clientes, Jogador jogador) {
        for (ClientCallback c : clientes) {
            try {
                c.onEndGame(this.notificarFimJogo(jogador));
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
    public String notificarFimJogo(Jogador jogador) {
        return """ 
        ======================================================================
        =                                                                    =
        =                           FIM DE JOGO!!                            =
        =                %s Conquistou todos os territórios.                 =
        =                                                                    =
        ======================================================================
        """.formatted(jogador.getNome());
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

    /**
     * @param jogadorDefesa Nome do jogador que está sob ataque.
     * @param territorio Nome do território que está sob ataque.
     * @return Mensagem de aviso: território sob ataque.
     * */
    public String notificarAtaque(String jogadorDefesa, String territorio) {
        return """ 
                ======================================================================
                                        NOVA AÇÃO REALIZADA!                                        
                                   %s : Seu território %s está sendo atacado!                         
                ======================================================================
                """.formatted(jogadorDefesa, territorio);
    }

    /**
     * @param resultados Mensagem personalizada com os resultados do último ataque.
     * @return Mensagem de aviso: ataque finalizado.
     * */
    public String notificarResultadoAtaque(String resultados) {
        return """ 
                ======================================================================
                                        ATAQUE FINALIZADO!                                      
                                  %s                       
                ======================================================================
                """.formatted(resultados);
    }

    /**
     * @param nome Nome do jogador atual para informar quem realizou a ação acionada.
     * @param territorioOrigem O território inicial das tropas.
     * @param territorioDestino O território de destino das tropas.
     * @param totalTropas A quantidade de tropas adicionadas.
     * @return Mensagem de aviso: nova ação - adição de tropas em um território.
     * */
    public String tropasMovimentadas(String nome, String territorioOrigem, String territorioDestino, Integer totalTropas) {
        return """ 
                ======================================================================                                                                   
                                         NOVA AÇÃO REALIZADA!                         
                                  O Jogador %s moveu suas tropas.                  
                                      %s >> %s (%d tropas).                     
                ======================================================================
                """.formatted(nome, territorioOrigem, territorioDestino, totalTropas);
    }

    public String jogadorDerrotado(String nomeJogador) {
        return """
                ======================================================================                                                                                            
                                  O Jogador %s foi derrotado!                  
                ======================================================================
                """.formatted(nomeJogador);
    }
}
