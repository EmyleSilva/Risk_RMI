package com.RiskRmi.server;

import com.RiskRmi.Rmi.ClientCallback;
import com.RiskRmi.enuns.FasesJogo;
import com.RiskRmi.enuns.Territorios;
import com.RiskRmi.enuns.TipoCarta;
import com.RiskRmi.enuns.TipoTropa;
import com.RiskRmi.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

    private Map<String, Territorio> territorios;
    private List<Jogador> jogadores;
    private List<Continente> continentes;
    private Stack<FasesJogo> fasesPorTurno;
    private List<Integer> ataquesResultados;
    private List<Integer> defesasResultados;
    private Map<String, Tropa> tropas;
    private List<Carta> baralho;
    private int jogadorAtualIndex = 0;
    private final int TAMANHO_BARALHO;
    private Dado dado;
    private Boolean jogoIniciado = false;
    private final int MIN_JOGADORES = 2;
    private final List<ClientCallback> clientes;
    private final NotificacoesCallback notificador;
    private Validate validator;

    public GameManager(int TAMANHO_BARALHO, List<ClientCallback> clientes) {
        this.TAMANHO_BARALHO = TAMANHO_BARALHO;
        this.dado = new Dado();
        this.jogadores = new ArrayList<>();
        this.clientes = clientes;
        this.notificador = new NotificacoesCallback();
        this.fasesPorTurno = new Stack<>();
    }

    /**
     * Registra um novo cliente no servidor.
     * @param cliente Novo cliente.
     * */
    public void registrarCliente(ClientCallback cliente) {
        clientes.add(cliente);
        System.out.println("Novo cliente registrado.\n");
    }

    /**
     * Remove um cliente do servidor (para evitar erros caso o cliente caia)
     * @param cliente Cliente a ser removido.
     * */
    public void removerCliente(ClientCallback cliente) {
        clientes.remove(cliente);
        System.out.println("Cliente removido da lista!\n");
    }

    /**
     * Atualiza a flag de sinalização de inicio do jogo quando a quantidade mínima de jogadores
     * é antigida.
     * Além disso, inicia o processo de criação do jogo através da chamada do metodo criarJogo()
     * */
    public void verificarInicioJogo(List<ClientCallback> clientes) {
        if (this.jogadores.size() >= MIN_JOGADORES && !jogoIniciado) {
            criarJogo(clientes);
            jogoIniciado = true;
            System.out.println("Jogo Iniciado!");
            notificador.callback(clientes, notificador.jogoIniciado());
            notificarPosicionamentoInicial();
        }else {
            notificador.callback(clientes, notificador.aguardandoJogadores());
        }
    }

    /*******************************************************
     *            MÉTODOS DE INICIALIZAÇÃO DO JOGO
     *******************************************************/

    /**
     * Metodo principal de criação de jogo, faz todas as chamadas aos metodos de criação (tropas, territorios, etc);
     * Também é responsável por distribuir os territórios entre os jogadores conectados e definir a quantidade de
     * tropas iniciais de cada um.
     * */
    public void criarJogo(List<ClientCallback> clientes) {
          criarTropas();
          criarTerritorios();
          criarContinentes();
          criarBaralho();
          criarPilhaFases(true);

          distribuirTerritorios();

        /**Calcula a quantidade inicial de tropas de cada jogador */
        Integer totalTropas = calcularTropasIniciais(jogadores.size());
        for (Jogador j : jogadores) {
            j.setTropasDisponiveis(totalTropas - j.getTerritorios().size());
        }
        System.out.println("ID do Jogador Atual: " + jogadores.get(jogadorAtualIndex).getId());

        /** Intanciar Validator */
        this.validator = new Validate(tropas, territorios);
    }

    /**
     * Cria todos os territórios do jogo, e os armazena em um hash map.
     * Cada território pode ser acessado com uma String que indica seu nome.
     *
     * Também realiza a inicialização das tropas presentes em cada território.
     * */
    public void criarTerritorios() {
        territorios = new HashMap<>();

        territorios.put(Territorios.BRASIL.getNome(), new Territorio(Territorios.BRASIL,
                List.of(Territorios.COLOMBIA, Territorios.VENEZUELA, Territorios.ARGELIA, Territorios.CONGO)
        ));

        territorios.put(Territorios.VENEZUELA.getNome(), new Territorio(Territorios.VENEZUELA,
                List.of(Territorios.COLOMBIA, Territorios.BRASIL, Territorios.ESPANHA)
        ));

        territorios.put(Territorios.COLOMBIA.getNome(), new Territorio(Territorios.COLOMBIA,
                List.of(Territorios.BRASIL, Territorios.VENEZUELA, Territorios.JAPAO)
        ));

        territorios.put(Territorios.ARGELIA.getNome(), new Territorio(Territorios.ARGELIA,
                List.of(Territorios.EGITO, Territorios.CONGO, Territorios.ESPANHA, Territorios.ALEMANHA)
        ));

        territorios.put(Territorios.EGITO.getNome(), new Territorio(Territorios.EGITO,
                List.of(Territorios.ARGELIA, Territorios.QUENIA, Territorios.CHINA)
        ));

        territorios.put(Territorios.CONGO.getNome(), new Territorio(Territorios.CONGO,
                List.of(Territorios.ARGELIA, Territorios.QUENIA, Territorios.BRASIL)
        ));

        territorios.put(Territorios.QUENIA.getNome(), new Territorio(Territorios.QUENIA,
                List.of(Territorios.EGITO, Territorios.CONGO, Territorios.CHINA)
        ));

        territorios.put(Territorios.ESPANHA.getNome(), new Territorio(Territorios.ESPANHA,
                List.of(Territorios.VENEZUELA, Territorios.ARGELIA, Territorios.ALEMANHA)
        ));

        territorios.put(Territorios.ALEMANHA.getNome(), new Territorio(Territorios.ALEMANHA,
                List.of(Territorios.ESPANHA, Territorios.ARGELIA, Territorios.CHINA)
        ));

        territorios.put(Territorios.CHINA.getNome(), new Territorio(Territorios.CHINA,
                List.of(Territorios.EGITO, Territorios.ALEMANHA, Territorios.JAPAO, Territorios.QUENIA)
        ));

        territorios.put(Territorios.JAPAO.getNome(), new Territorio(Territorios.JAPAO,
                List.of(Territorios.COLOMBIA, Territorios.CHINA)
        ));

        for (String s : territorios.keySet()) {
            territorios.get(s).inicializarTropas(tropas);
        }
    }

    /**
     * Cria todos os contininentes do jogo.
     * Adiciona todos os continentes criados em um Array do jogo.
     * */
    public void criarContinentes() {

        continentes = new ArrayList<>();

        continentes.add(new Continente("America",
                List.of(territorios.get(Territorios.BRASIL.getNome()), territorios.get(Territorios.VENEZUELA.getNome()), territorios.get(Territorios.COLOMBIA.getNome())),
                4));

        continentes.add(new Continente("Africa",
                List.of(territorios.get(Territorios.ARGELIA.getNome()), territorios.get(Territorios.EGITO.getNome()), territorios.get(Territorios.CONGO.getNome()), territorios.get(Territorios.QUENIA.getNome())),
                5));

        continentes.add(new Continente("Europa",
                List.of(territorios.get(Territorios.ESPANHA.getNome()), territorios.get(Territorios.ALEMANHA.getNome())),
                2));

        continentes.add(new Continente("Asia",
                List.of(territorios.get(Territorios.CHINA.getNome()), territorios.get(Territorios.JAPAO.getNome())),
                2));
    }

    /**
     * Cria todas as tropas e as adiciona em um hashMap do jogo.
     * */
    public void criarTropas() {
        tropas = new HashMap<>();

        tropas.put(TipoTropa.INFANTARIA.getNome(), new Tropa(TipoTropa.INFANTARIA, 1));
        tropas.put(TipoTropa.CAVALARIA.getNome(), new Tropa(TipoTropa.CAVALARIA, 5));
    }

    /**
     * Responsável por criar o baralho do jogo. Para isso, pega todos os tipos possiveis de cartas,
     * depois adiciona uma nova carta (os tipos são escolhidos de forma proporcional) ao deck de cartas
     * do jogo. A quantidade de cartas é definida pela constante TAMANHO_BARALHO.
     *
     * No fim da distribuição, realiza o embaralhamento das cartas.
     * */
    public void criarBaralho() {
        baralho = new ArrayList<>();

        /** Recupera todos os tipos de cartas disponíveis */
        TipoCarta[] tipos = TipoCarta.values();

        /** Gera uma quantidade equilibrada de cada tipo de carta */
        for (int i = 0; i < TAMANHO_BARALHO; i++) {
            TipoCarta tipo = tipos[i % tipos.length];
            baralho.add(new Carta(tipo));
        }
        /** Embaralha o Deck */
        Collections.shuffle(baralho);
    }

    /**
     * Cria uma pilha com as fases de um turno para controle do jogo.
     * Leva em consideração as seguintes condições de turno para o empilhamento:
     *
     * Se é o ínicio do jogo (todos os jogadores acabaram de se conectar), empilha a fase POSICIONAMENTO_INICIAL.
     * Se acabou de sair da fase de posicionamento Incial, não existe bonificação de tropas, então empilha apenas ATAQUE E MOVIMENTAÇÃO
     * Caso contrário, se trata de um turno normal, então empilha as fases de POSICIONAMENTO, ATAQUE e MOVIMENTAÇÃO.
     *
     * @param inicioJogo Flag que indica se é ou não inicio do jogo.
     * */
    public void criarPilhaFases(Boolean inicioJogo) {
        if (inicioJogo) {
            fasesPorTurno.push(FasesJogo.POSICIONAMETO_INICAL);
        }else if (fasesPorTurno.peek() != FasesJogo.POSICIONAMETO_INICAL) {
            fasesPorTurno.clear();
            fasesPorTurno.push(FasesJogo.MOVIMENTACAO);
            fasesPorTurno.push(FasesJogo.ATAQUE);
            fasesPorTurno.push(FasesJogo.POSCIONAMENTO);
        }else {
            fasesPorTurno.clear();
            fasesPorTurno.push(FasesJogo.MOVIMENTACAO);
            fasesPorTurno.push(FasesJogo.ATAQUE);
        }
    }

    /**
     * Distribui os territórios do jogo entre todos os jogadores. A distribuição é feita de maneira proporcional
     * ao número de jogadores e territórios, para que no inicio a quantidade de territórios de cada jogador
     * seja equilibrada.
     * */
    public void distribuirTerritorios() {
        List<Territorio> listaTerritorios = new ArrayList<>(territorios.values());

        Collections.shuffle(listaTerritorios);

        int i = 0;

        for (Territorio t : listaTerritorios) {
            Jogador jogador = jogadores.get(i % jogadores.size());

            t.setDono(jogador);
            t.adicionarTropas(tropas, 1);
            jogador.adicionarTerritorio(t);

            i++;
        }
    }

    /**
     * Calcula a quantidade total de tropas iniciais dos jogadores.
     * A quantidade de tropas depende da quantidade de jogadores na sessão, sendo
     * o mínimo de 2 jogadores com 20 tropas e máximo de 4 jogadores com 10 tropas.
     *
     * @param quantidadeJogadores O total de jogadores na sessão.
     * @return int Valor total de tropas para cada jogador.
     * */
    public Integer calcularTropasIniciais(int quantidadeJogadores) {
        switch (quantidadeJogadores) {
            case 2: return 20;
            case 3: return 15;
            case 4: return 10;
            default: throw new IllegalArgumentException();
        }
    }

    /*******************************************************
     *            MÉTODOS DE CONTROLE DO JOGO
     *******************************************************/

    /**
     * Posiciona uma determinada quantidade de tropas em um território.
     * Só ocorre no inicio do jogo para a distribuição inicial das tropas recebidas pelos jogadores.
     *
     * @param jogadorId O id do Jogador que quer realizar o posicionamento.
     * @param nomeTerritorio O territorio para adicionar tropas.
     * @param quantidadeTropas A quantidade de tropas que deve ser adicionada.
     * */
    public void posicionamentoInicial(int jogadorId, String nomeTerritorio, int quantidadeTropas) {
        Jogador jogador = jogadores.get(jogadorId-1);
        Territorio territorio = territorios.get(nomeTerritorio);

        validator.validarFaseAtual(fasesPorTurno, FasesJogo.POSICIONAMETO_INICAL);
        validator.validarTurnoJogador(jogadores, jogadorId, jogadorAtualIndex);
        validator.validarTerritorioJogador(territorio, jogador);
        validator.validarTropasDisponiveis(quantidadeTropas, jogador);

        territorio.adicionarTropas(tropas, quantidadeTropas);
        jogador.setTropasDisponiveis(jogador.getTropasDisponiveis()-quantidadeTropas);

        notificador.callback(clientes, notificador.tropasAdicionadas(jogador.getNome(), nomeTerritorio, territorio.getTotalTropas(tropas)));

        if (jogador.getTropasDisponiveis() == 0) {
            if (ultimoDaRodada()) {
                passarVez(jogadorId);
            }else {
                jogadorAtualIndex++;
                notificarPosicionamentoInicial();
            }
        }
    }

    /**
     * Busca os nomes de todos os territorios do jogador,
     * juntamente com a quantidade total de cada tipo tropa presente no territorio.
     *
     * @param jogadorId Jogador que realizou a requisição.
     * @return Uma lista com todos os nomes e tropas dos territorios.
     * */
    public List<String> buscarTerritoriosTropasJogador(int jogadorId) {
        return jogadores.get(jogadorId-1).buscarTerritoriosTropas(tropas);
    }

    /**
     * Busca a quantidade total de tropas que o jogador ainda tem disponivel para posicionamento inicial.
     * @param jogadorId Id do Jogador que realizou a requisição.
     * @return A quantidade de tropas disponiveis.
     * */
    public Integer buscarTropasDisponiveisJogador(int jogadorId) {
        return jogadores.get(jogadorId-1).getTropasDisponiveis();
    }

    /**
     * Retorna a quantidade total de tropas (cavalaria + infantaria) do territorio
     * @param nomeTerritorio O territorio para busca do total.
     * @return A quantidade total de tropas.
     * */
    public Integer totalTropasTerritorio(String nomeTerritorio) {
        Territorio territorio = territorios.get(nomeTerritorio);
        return territorio.getTotalTropas(tropas);
    }

    /**
     * Metodo que realiza todo o fluxo de ataque.
     * Realiza todas as devidas validações para integridade do jogo,
     * define com quantas tropas os jogadores irão atacar/defender com base na quantidade de tropas
     * nos respectivos territórios. Lança os dados de forma aleatória, realiza a comparação, e calcula a
     * quantidade de perdas dos dois lados com base na comparação dos dados.
     *
     * Verifica se o território foi conquistado pelo atacante e, caso sim, realiza a mudança de dono do território.
     * Também notifica todos os jogadores das ações importantes processadas durante os ataques.
     *
     * @param jogadorId O id do jogador que está atacando.
     * @param origem Nome do território do jogador atacante.
     * @param destino Nome do território que o jogador quer atacar.
     * */
    public void atacar(int jogadorId, String origem, String destino) {
        Territorio territorioOrigem = territorios.get(origem);
        Territorio territorioDestino = territorios.get(destino);
        Jogador jogador = jogadores.get(jogadorId-1);
        Jogador jogadorAtacado = territorioDestino.getDono();

        validator.validarTurnoJogador(jogadores, jogadorId, jogadorAtualIndex);
        validator.validarTerritorioJogador(territorioOrigem, jogador);
        validator.validarVizinho(territorioOrigem, territorioDestino);
        validator.validarQuantidadeTropas(true, territorioOrigem);

        System.out.println("Ataque Iniciado....");

        notificador.callback(clientes, notificador.notificarAtaque(jogadorAtacado.getNome(), destino));

        //Calcular quantas tropas de defesa
        int tropasAtaque = calcularTropasAtaque(territorioOrigem);
        int tropasDefesa = calcularTropasDefesa(territorioDestino);
        int menor = (tropasAtaque < tropasDefesa) ? tropasAtaque : tropasDefesa;

        //Rolar dados de ataque e defesa
        ataquesResultados = dado.rolarDados(tropasAtaque);
        defesasResultados = dado.rolarDados(tropasDefesa);

        int perdasAtaque = 0;
        int perdasDefesa = 0;

        //Comparar dados
        for (int i = 0; i < menor; i++) {
            if (ataquesResultados.get(i) > defesasResultados.get(i)) {
                perdasDefesa++;
            }else if (ataquesResultados.get(i) <= defesasResultados.get(i)){ //TODO:Tirar esse condicional desnecessário
                perdasAtaque++;
            }
        }

        //Remove as tropas perdidas dos territórios
        if (perdasAtaque > 0) {
            territorioOrigem.retirarTropas(tropas, perdasAtaque);
        }
        if (perdasDefesa > 0) {
            territorioDestino.retirarTropas(tropas, perdasDefesa);
        }

        /** TODO: Melhorar essa mensagem (contéudo e ordem que as coisas aparecem) **/
        /** Verifica se houve captura de território, se sim, altera o dono */
        final boolean territorioCapturado = territorioDestino.verificarCaptura(tropas);
        String mensagem = "";
        if (territorioCapturado) {
            territorioDestino.setDono(jogador);
            jogadorAtacado.getTerritorios().remove(territorioDestino);
            jogador.getTerritorios().add(territorioDestino);
            territorioDestino.adicionarTropas(tropas,tropasAtaque - perdasAtaque);
            //Com a conquista do território, o jogador ganha uma carta do baralho

            mensagem = mensagem + """
                        %s Capturou o território %s!
                        Você ganhou 1 carta pela conquista.
                    """.formatted(jogador.getNome(), destino);

        }

        mensagem = mensagem + "Dados Ataque: " + ataquesResultados.stream().map(Object::toString).collect(Collectors.joining(", "))
                   + "\n Dados Defesa: " + defesasResultados.stream().map(Object::toString).collect(Collectors.joining(", "))
                   + "\n"+ jogador.getNome() + " perdeu " + perdasAtaque + " \n" + jogadorAtacado.getNome() + " perdeu " + perdasDefesa;

        notificador.callback(clientes, notificador.notificarResultadoAtaque(mensagem));

        System.out.println("Ataque Finalizado...");
    }

    /**
     * Passa para a próxima fase do jogo. Mantém o mesmo jogador se ainda for uma fase do mesmo turno,
     * caso seja a última fase (Movimentação), automaticamente passa para o turno do próximo jogador.
     *
     * @param jogadorId Jogador que solicitou a mudança de fase.
     * */
    public void proximaFase(int jogadorId) {
        validator.validarTurnoJogador(jogadores, jogadorId, jogadorAtualIndex);

        if (fasesPorTurno.peek() == FasesJogo.MOVIMENTACAO) {
            passarVez(jogadorAtualIndex);
        }else {
            fasesPorTurno.pop();
            notificador.callback(clientes, notificador.novaFase(jogadores.get(jogadorAtualIndex).getNome(), fasesPorTurno.peek()));
        }
    }

    /**
     * Busca todos os territórios criados no jogo.
     *
     * @return Uma lista com os nomes dos territórios existentes.
     * */
    public List<Territorio> buscarTerritorios() {
        List<Territorio> territorios = new ArrayList<>();

        for (String s : this.territorios.keySet()) {
            territorios.add(this.territorios.get(s));
        }

        return territorios;
    }


    /*******************************************************
     *            MÉTODOS AUXILIARES
     *******************************************************/

    /**
     * Busca os nomes de todos os territórios de um jogador.
     * @param jogadorId Jogador que realizou a requisição.
     * @return Uma lista com todos os nomes dos territorios.
     * */
    public List<String> buscarTerritoriosJogador(int jogadorId) {
        return jogadores.get(jogadorId-1).buscarTerritorios();
    }

    /**
     * Passa a vez para o próximo jogador na fila.
     * Caso seja o ultimo jogador, retorna para o primeiro da fila.
     * Sempre reinicia a pilha de fases para cada novo jogador.
     *
     * TODO: Não permitir passar a vez manualmente no turno de Posicionamento Inicial.
     *
     * @param jogadorId O id do jogador que realizou a requisição direta ou indiretamente
     *                  (quando acabam as peças do posicionamento inicial).
     * */
    public void passarVez(int jogadorId) {
        validator.validarTurnoJogador(jogadores, jogadorId, jogadorAtualIndex);

        if (ultimoDaRodada()) jogadorAtualIndex = 0;
        else jogadorAtualIndex++;

        final String nomeJogadorAtual = jogadores.get(jogadorAtualIndex).getNome();
        criarPilhaFases(false);
        notificador.callback(clientes, notificador.novaFase(nomeJogadorAtual, fasesPorTurno.peek()));
    }

    /**
     * Informa aos jogadores que o jogo iniciou a fase de Posicionamento atual.
     * Informa também qual o jogador da vez.
     * */
    public void notificarPosicionamentoInicial() {
        String nomeJogadorAtual = jogadores.get(jogadorAtualIndex).getNome();
        notificador.callback(clientes, notificador.posicionamentoInicial(nomeJogadorAtual));
    }

    /**
     * Verifica se já está no ultimo jogador da fila.
     * @return true se for o último jogador, false caso contrário.
     * */
    public boolean ultimoDaRodada() {
        return (jogadorAtualIndex + 1) == jogadores.size();
    }

    /**
     * Seleciona a quantidade de tropas que serão usadas para atacar com base
     * na quantidade de tropas totais presentes no território de origem do ataque.
     * Sempre retorna a maior quantidade possível.
     *
     * @param territorio O território de origem do ataque.
     * @return A quantidade máxima de tropas de ataque.
     * */
    public int calcularTropasAtaque(Territorio territorio) {
        int totalTropas = territorio.getTotalTropas(tropas);

        if (totalTropas > 3) return 3;
        if (totalTropas == 3) return 2;
        return 1;
    }

    /**
     * Seleciona a quantidade de tropas que serão usadas para defender com base na quantidade de
     * tropas totais presentes no território que será atacado. Sempre retorna a maior quantidade
     * possível.
     *
     * @param territorio O território que será atacado.
     * @return A quantidade máxima de tropas.
     * */
    public int calcularTropasDefesa(Territorio territorio) {
        int totalTropas = territorio.getTotalTropas(tropas);

        if (totalTropas >= 2) return 2;
        return 1;
    }
    /*******************************************************
     *                GETTERS E SETTERS
     *******************************************************/
    public Map<String, Territorio> getTerritorios() {
        return territorios;
    }

    public void setTerritorios(Map<String, Territorio> territorios) {
        this.territorios = territorios;
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<Jogador> jogadores) {
        this.jogadores = jogadores;
    }

    public List<Continente> getContinentes() {
        return continentes;
    }

    public void setContinentes(List<Continente> continentes) {
        this.continentes = continentes;
    }

    public Stack<FasesJogo> getFasesPorTurno() {
        return fasesPorTurno;
    }

    public void setFasesPorTurno(Stack<FasesJogo> fasesPorTurno) {
        this.fasesPorTurno = fasesPorTurno;
    }

    public List<Integer> getAtaquesResultados() {
        return ataquesResultados;
    }

    public void setAtaquesResultados(List<Integer> ataquesResultados) {
        this.ataquesResultados = ataquesResultados;
    }

    public List<Integer> getDefesasResultados() {
        return defesasResultados;
    }

    public void setDefesasResultados(List<Integer> defesasResultados) {
        this.defesasResultados = defesasResultados;
    }

    public Map<String, Tropa> getTropas() {
        return tropas;
    }

    public void setTropas(Map<String, Tropa> tropas) {
        this.tropas = tropas;
    }

    public List<Carta> getBaralho() {
        return baralho;
    }

    public void setBaralho(List<Carta> baralho) {
        this.baralho = baralho;
    }

    public int getJogadorAtualIndex() {
        return jogadorAtualIndex;
    }

    public void setJogadorAtualIndex(int jogadorAtualIndex) {
        this.jogadorAtualIndex = jogadorAtualIndex;
    }

    public int getTAMANHO_BARALHO() {
        return TAMANHO_BARALHO;
    }

    public Dado getDado() {
        return dado;
    }

    public void setDado(Dado dado) {
        this.dado = dado;
    }

    @Override
    public String toString() {
        return "GameManager{" +
                "jogoIniciado=" + jogoIniciado +
                ", baralho=" + baralho +
                ", tropas=" + tropas +
                ", defesasResultados=" + defesasResultados +
                ", ataquesResultados=" + ataquesResultados +
                ", fasesPorTurno=" + fasesPorTurno +
                ", continentes=" + continentes +
                ", jogadores=" + jogadores +
                ", territorios=" + territorios +
                '}';
    }
}
