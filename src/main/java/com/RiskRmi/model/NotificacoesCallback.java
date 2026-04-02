package com.RiskRmi.model;

import com.RiskRmi.Rmi.ClientCallback;

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
}
