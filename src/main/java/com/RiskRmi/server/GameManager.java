package com.RiskRmi.server;

import com.RiskRmi.Rmi.ClientCallback;
import com.RiskRmi.enums.FasesJogo;
import com.RiskRmi.enums.Territorios;
import com.RiskRmi.enums.TipoCarta;
import com.RiskRmi.enums.TipoTropa;
import com.RiskRmi.exceptions.InvalidActionException;
import com.RiskRmi.model.*;

import java.rmi.RemoteException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GameManager {

    private Map<String, Territorio> territorios;
    private List<Jogador> jogadores;
    private List<Continente> continentes;
    private Stack<FasesJogo> fasesPorTurno;
    private List<Integer> ataquesResultados;
    private List<Integer> defesasResultados;
    private List<TipoTropa> tropas;
    private List<TipoCarta> baralho;
    private int jogadorAtualIndex = 0;
    private final int TAMANHO_BARALHO;
    private final Dado dado;
    private Boolean jogoIniciado = false;
    private final int QUANTIDADE_JOGADORES = 2;
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
     * Remove um jogador da partida, mantém o cliente associado como listener para que ele continue
     * acompanhando a partida.
     * @param jogador O jogador derrotado.
     * @param mensagem Uma mensagem de fim de jogo para o jogador.
     * */
    public void removerJogadorCliente(Jogador jogador, String mensagem) throws RemoteException{
        ClientCallback clienteAssociado = jogador.getClienteAssociado();
        clienteAssociado.onEndGame(mensagem);
        jogadores.remove(jogador);
    }

    /**
     * Atualiza a ‘flag’ de sinalização de início do jogo quando a quantidade mínima de jogadores
     * é atingida.
     * Além disso, inicia o processo de criação do jogo através da chamada do método criarJogo() e
     * inicializa o jogo para cada cliente.
     * */
    public void verificarInicioJogo() {
        if (this.jogadores.size() >= QUANTIDADE_JOGADORES && !jogoIniciado) {
            criarJogo();
            jogoIniciado = true;
            System.out.println("Jogo Iniciado!");
            notificador.callback(clientes, notificador.jogoIniciado());
            iniciarClientes();
            notificarPosicionamentoInicial();
        }else {
            notificador.callback(clientes, notificador.aguardandoJogadores());
        }
    }

    /**
     * Inicializa o jogo para cada cliente
     * */
    public void iniciarClientes() {
        for (Jogador j : jogadores) {
            try {
                ClientCallback c = j.getClienteAssociado();
                c.onStartGame(j.getId());
            }catch (RemoteException e) {
                System.out.println("Erro: " + e.getMessage() + " ao notificar cliente.");
                clientes.remove(j.getClienteAssociado());
                System.out.println("Cliente removido do servidor.\n");
            }
        }
    }

    /*******************************************************
     *            MÉTODOS DE INICIALIZAÇÃO DO JOGO
     *******************************************************/

    /**
     * Método principal de criação de jogo, faz todas as chamadas aos métodos de criação (tropas, territorios, etc);
     * Também é responsável por distribuir os territórios entre os jogadores conectados e definir a quantidade de
     * tropas iniciais de cada um.
     * */
    public void criarJogo() {
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

        /** Instanciar Validator */
        this.validator = new Validate();
    }

    /**
     * Cria todos os territórios do jogo, e os armazena em um hash map.
     * Cada território pode ser acessado com uma String que indica o seu nome.
     *
     * Também realiza a inicialização das tropas presentes em cada território.
     * */
    public void criarTerritorios() {
        territorios = new HashMap<>();

        territorios.put(Territorios.BARATIE.getNome(), new Territorio(Territorios.BARATIE,
                List.of(Territorios.FOOSHA, Territorios.OHARA, Territorios.ARLONG)));

        territorios.put(Territorios.ARLONG.getNome(), new Territorio(Territorios.ARLONG,
                List.of(Territorios.BARATIE, Territorios.FOOSHA, Territorios.WHITE)));

        territorios.put(Territorios.FOOSHA.getNome(), new Territorio(Territorios.FOOSHA,
                List.of(Territorios.BARATIE, Territorios.ARLONG, Territorios.WANO)));

        territorios.put(Territorios.WHITE.getNome(), new Territorio(Territorios.WHITE,
                List.of(Territorios.SPIDER, Territorios.ARLONG, Territorios.OHARA)));

        territorios.put(Territorios.SPIDER.getNome(), new Territorio(Territorios.SPIDER,
                List.of(Territorios.WHITE, Territorios.KANO, Territorios.WHOLECAKE)));

        territorios.put(Territorios.OHARA.getNome(), new Territorio(Territorios.OHARA,
                List.of(Territorios.KANO, Territorios.GODVALLEY, Territorios.BARATIE, Territorios.WHITE)));

        territorios.put(Territorios.KANO.getNome(), new Territorio(Territorios.KANO,
                List.of(Territorios.OHARA, Territorios.GODVALLEY, Territorios.SPIDER)));

        territorios.put(Territorios.GODVALLEY.getNome(), new Territorio(Territorios.GODVALLEY,
                List.of(Territorios.OHARA, Territorios.KANO, Territorios.ENIES)));

        territorios.put(Territorios.WHOLECAKE.getNome(), new Territorio(Territorios.WHOLECAKE,
                List.of(Territorios.WANO, Territorios.SKYPIEA, Territorios.SPIDER)));

        territorios.put(Territorios.WANO.getNome(), new Territorio(Territorios.WANO,
                List.of(Territorios.WHOLECAKE, Territorios.ENIES, Territorios.FOOSHA)));

        territorios.put(Territorios.ENIES.getNome(), new Territorio(Territorios.ENIES,
                List.of(Territorios.WANO, Territorios.SKYPIEA, Territorios.GODVALLEY)));

        territorios.put(Territorios.SKYPIEA.getNome(), new Territorio(Territorios.SKYPIEA,
                List.of(Territorios.WHOLECAKE, Territorios.ENIES)));

        for (String s : territorios.keySet()) {
            territorios.get(s).inicializarTropas(tropas);
        }
    }

    /**
     * Cria todos os continentes do jogo.
     * Adiciona todos os continentes criados em um Array do jogo.
     * */
    public void criarContinentes() {

        continentes = new ArrayList<>();

        continentes.add(new Continente("East Blue",
                List.of(territorios.get(Territorios.BARATIE.getNome()), territorios.get(Territorios.ARLONG.getNome()), territorios.get(Territorios.FOOSHA.getNome())),
                3));

        continentes.add(new Continente("North Blue",
                List.of(territorios.get(Territorios.WHITE.getNome()), territorios.get(Territorios.SPIDER.getNome())),
                2));

        continentes.add(new Continente("West Blue",
                List.of(territorios.get(Territorios.OHARA.getNome()), territorios.get(Territorios.KANO.getNome()), territorios.get(Territorios.GODVALLEY.getNome())),
                3));

        continentes.add(new Continente("Grand Line",
                List.of(territorios.get(Territorios.WHOLECAKE.getNome()), territorios.get(Territorios.WANO.getNome()),
                        territorios.get(Territorios.ENIES.getNome()), territorios.get(Territorios.SKYPIEA.getNome())),
                5));
    }

    /**
     * Cria todas as tropas e as adiciona em um hashMap do jogo.
     * */
    public void criarTropas() {
        tropas = new ArrayList<>();
        tropas.add(TipoTropa.INFANTARIA);
        tropas.add(TipoTropa.CAVALARIA);
    }

    /**
     * Responsável por criar o baralho do jogo. Para isso, pega todos os tipos possíveis de cartas,
     * depois adiciona uma nova carta (os tipos são escolhidos de forma proporcional) ao deck de cartas
     * do jogo. A quantidade de cartas é definida pela constante TAMANHO_BARALHO.
     *
     * No fim da distribuição, embaralha as cartas.
     * */
    public void criarBaralho() {
        baralho = new ArrayList<>();

        /** Recupera todos os tipos de cartas disponíveis */
        TipoCarta[] tipos = TipoCarta.values();

        /** Gera uma quantidade equilibrada de cada tipo de carta */
        for (int i = 0; i < TAMANHO_BARALHO; i++) {
            TipoCarta tipo = tipos[i % tipos.length];
            baralho.add(tipo);
        }
        /** Embaralha o Deck */
        Collections.shuffle(baralho);
    }

    /**
     * Cria uma pilha com as fases de um turno para controle do jogo.
     * Leva em consideração as seguintes condições de turno para o empilhamento:
     *
     * Se é o ínicio do jogo (todos os jogadores acabaram de se conectar), empilha a fase POSICIONAMENTO_INICIAL.
     * Se acabou de sair da fase de posicionamento Inicial, não existe bonificação de tropas, então empilha apenas ATAQUE E MOVIMENTAÇÃO
     * Caso contrário, se trata de um turno normal, então empilha as fases de POSICIONAMENTO, ATAQUE e MOVIMENTAÇÃO.
     *
     * Quando está em um turno comum, também aciona o cálculo do bônus do jogador atual.
     *
     * @param inicioJogo Flag que indica se é ou não inicio do jogo.
     * */
    public void criarPilhaFases(Boolean inicioJogo) {
        if (inicioJogo) {
            fasesPorTurno.push(FasesJogo.POSICIONAMETO_INICAL);
        }else {
            fasesPorTurno.clear();
            fasesPorTurno.push(FasesJogo.MOVIMENTACAO);
            fasesPorTurno.push(FasesJogo.ATAQUE);
            fasesPorTurno.push(FasesJogo.POSCIONAMENTO);

            //Aciona o cálculo de bonificação de início de turno do jogador atual.
            calcularBonificacao(jogadores.get(jogadorAtualIndex).getId());
        }
    }

    /**
     * Distribui os territórios do jogo entre todos os jogadores. A distribuição é feita de maneira proporcional
     * ao número de jogadores e territórios, para que no início a quantidade de territórios de cada jogador
     * seja equilibrada.
     * */
    public void distribuirTerritorios() {
        List<Territorio> listaTerritorios = new ArrayList<>(territorios.values());

        Collections.shuffle(listaTerritorios);

        int i = 0;

        for (Territorio t : listaTerritorios) {
            Jogador jogador = jogadores.get(i % jogadores.size());

            t.setDono(jogador);
            t.adicionarTropas(1);
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
     * Só ocorre no início do jogo para a distribuição inicial das tropas recebidas pelos jogadores.
     *
     * @param jogadorId O id do Jogador que quer realizar o posicionamento.
     * @param nomeTerritorio O territorio para adicionar tropas.
     * @param quantidadeTropas A quantidade de tropas que deve ser adicionada.
     * */
    public void posicionamentoInicial(int jogadorId, String nomeTerritorio, int quantidadeTropas) {
        validator.validarTerritorio(nomeTerritorio, territorios);

        Jogador jogador = jogadores.get(buscarPosicaoJogador(jogadorId));
        Territorio territorio = territorios.get(nomeTerritorio);

        validator.validarFaseAtual(fasesPorTurno, FasesJogo.POSICIONAMETO_INICAL);
        validator.validarTurnoJogador(jogadores, jogadorId, jogadorAtualIndex);
        validator.validarTerritorioJogador(territorio, jogador);
        validator.validarTropasDisponiveis(quantidadeTropas, jogador);

        territorio.adicionarTropas(quantidadeTropas);
        jogador.setTropasDisponiveis(jogador.getTropasDisponiveis()-quantidadeTropas);

        notificador.callback(clientes, notificador.tropasAdicionadas(jogador.getNome(), nomeTerritorio, territorio.getTotalTropas()));

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
     * com a quantidade total de cada tipo tropa presente no territorio.
     *
     * @param jogadorId Jogador que realizou a requisição.
     * @return Uma lista com todos os nomes e tropas dos territorios.
     * */
    public List<String> buscarTerritoriosTropasJogador(int jogadorId) {
        return jogadores.get(buscarPosicaoJogador(jogadorId)).buscarTerritoriosTropas();
    }

    /**
     * Busca a quantidade total de tropas que o jogador ainda tem disponível para posicionamento inicial.
     * @param jogadorId Id do Jogador que realizou a requisição.
     * @return A quantidade de tropas disponíveis.
     * */
    public Integer buscarTropasDisponiveisJogador(int jogadorId) {
        return jogadores.get(buscarPosicaoJogador(jogadorId)).getTropasDisponiveis();
    }

    /**
     * Retorna a quantidade total de tropas (cavalaria + infantaria) do territorio
     * @param nomeTerritorio O territorio para busca do total.
     * @return A quantidade total de tropas.
     * */
    public Integer totalTropasTerritorio(String nomeTerritorio) {
        validator.validarTerritorio(nomeTerritorio, territorios);

        Territorio territorio = territorios.get(nomeTerritorio);
        return territorio.getTotalTropas();
    }

    /**
     * Método que realiza todo o fluxo de ataque.
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
    public void atacar(int jogadorId, String origem, String destino) throws RemoteException{
        validator.validarTerritorio(origem, territorios);
        validator.validarTerritorio(destino, territorios);

        Territorio territorioOrigem = territorios.get(origem);
        Territorio territorioDestino = territorios.get(destino);

        Jogador jogador = jogadores.get(buscarPosicaoJogador(jogadorId));
        Jogador jogadorAtacado = territorioDestino.getDono();

        validator.validarTurnoJogador(jogadores, jogadorId, jogadorAtualIndex);
        validator.validarFaseAtual(fasesPorTurno, FasesJogo.ATAQUE);
        validator.validarDonoDestino(jogador, territorioDestino);
        validator.validarTerritorioJogador(territorioOrigem, jogador);
        validator.validarVizinho(territorioOrigem, territorioDestino);
        validator.validarQuantidadeTropas(territorioOrigem);

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
            }else {
                perdasAtaque++;
            }
        }

        //Remove as tropas perdidas dos territórios
        if (perdasAtaque > 0) {
            territorioOrigem.retirarTropas(perdasAtaque);
        }
        if (perdasDefesa > 0) {
            territorioDestino.retirarTropas(perdasDefesa);
        }

        /** Verifica se houve captura de território, se sim, altera o dono */
        final boolean territorioCapturado = territorioDestino.verificarCaptura();
        String mensagem = "";
        if (territorioCapturado) {
            territorioDestino.setDono(jogador);
            jogadorAtacado.getTerritorios().remove(territorioDestino);
            jogador.getTerritorios().add(territorioDestino);
            territorioDestino.adicionarTropas(tropasAtaque - perdasAtaque);
            //Com a conquista do território, o jogador ganha uma carta do baralho
            jogador.adicionarCarta(baralho.getFirst());
            baralho.removeFirst(); //Retira a carta do deck

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

        verificarFimJogo(jogadorId);
        verificarRemoverJogador(jogadorAtacado);
    }

    /**
     * Verifica se o Jogador que realizou o ataque mais recente possui todos os territórios do jogo.
     * Caso sim, encerra o jogo!
     * @param jogadorId O id do jogador que realizou o último ataque.
     * */
    public void verificarFimJogo(int jogadorId) {
        Jogador jogador = jogadores.get(buscarPosicaoJogador(jogadorId));
        if (jogador.getTerritorios().size() == this.territorios.size()) {
            notificador.callback(clientes, jogador);
        }
    }

    /**
     * Verifica se, após um ataque, o jogador ainda possui territórios.
     * Se não possuir, significa que foi derrotado e é removido do jogo.
     * Todos os jogadores são notificados da derrota do jogador.
     * @param jogador O último jogador atacado.
     * */
    public void verificarRemoverJogador(Jogador jogador) throws RemoteException{
        if (jogador.getTerritorios().isEmpty()) {
            notificador.callback(clientes, notificador.jogadorDerrotado(jogador.getNome()));
            removerJogadorCliente(jogador, "Você perdeu!");
        }
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
            passarVez(jogadorId);
        }else {
            validator.validarPassarFaseInicial(jogadores.get(buscarPosicaoJogador(jogadorId)), jogadores.getLast().getId(),fasesPorTurno.peek());
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


    /**
     * TODO: Adicionar possibilidade de ir fortificar territórios que não são diretamente vizinhos, mas filho de algum vizinho
     * Realiza a movimentação de tropas (fortificação) após a fase de ataque.
     *
     * @param jogadorId O id jogador que deseja realizar a movimentação.
     * @param origem O território de origem das tropas.
     * @param destino O território que será fortificado.
     * @param quantidadeTropas A quantidade de tropas que será movida.
     * */
    public void movimentarTropas(int jogadorId, String origem, String destino, Integer quantidadeTropas) {
        validator.validarTerritorio(origem, territorios);
        validator.validarTerritorio(destino, territorios);

        Territorio tOrigem = territorios.get(origem);
        Territorio tDestino = territorios.get(destino);
        Jogador jogador = jogadores.get(buscarPosicaoJogador(jogadorId));

        validator.validarTurnoJogador(jogadores, jogadorId, jogadorAtualIndex);
        validator.validarFaseAtual(fasesPorTurno, FasesJogo.MOVIMENTACAO);
        validator.validarTerritorioJogador(tOrigem, jogador);
        validator.validarTerritorioJogador(tDestino, jogador);
        validator.validarVizinho(tOrigem, tDestino);
        validator.validarQuantidadeTropas(quantidadeTropas, tOrigem);

        //realizar movimentação
        tOrigem.retirarTropas(quantidadeTropas);
        tDestino.adicionarTropas(quantidadeTropas);

        tDestino.reorganizarTropas();

        notificador.callback(clientes, notificador.tropasMovimentadas(jogador.getNome(), origem, destino, tDestino.getTotalTropas()));
        proximaFase(jogadorId);
    }

    /**
     * @return Retorna uma lista com todas as cartas que o jogador possui.
     * */
    public List<String> buscarCartasJogador(int jogadorId) {
        Jogador jogador = jogadores.get(buscarPosicaoJogador(jogadorId));
        return jogador.getCartasNomes();
    }

    public List<String> buscarTerritoriosInimigos(int jogadorId) {
        List<String> territorios = new ArrayList<>();
        Jogador jogador = jogadores.get(buscarPosicaoJogador(jogadorId));

        for (String t : this.territorios.keySet()) {
            Territorio objectT = this.territorios.get(t);
            if (objectT.getDono() != jogador) {
                territorios.add(t);
            }
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
        return jogadores.get(buscarPosicaoJogador(jogadorId)).buscarTerritorios();
    }

    /**
     * Passa a vez para o próximo jogador na fila.
     * Caso seja o último jogador, retorna para o primeiro da fila.
     * Sempre reinicia a pilha de fases para cada novo jogador.
     *
     * @param jogadorId O id do jogador que realizou a requisição direta ou indiretamente
     *                  (quando acabam as peças do posicionamento inicial).
     * */
    public void passarVez(int jogadorId) {
        validator.validarTurnoJogador(jogadores, jogadorId, jogadorAtualIndex);
        validator.validarPassarVezInicial(jogadores.get(buscarPosicaoJogador(jogadorId)), fasesPorTurno.peek());
        if (ultimoDaRodada()) jogadorAtualIndex = 0;
        else jogadorAtualIndex++;

        final String nomeJogadorAtual = jogadores.get(jogadorAtualIndex).getNome();
        criarPilhaFases(false);

        notificador.callback(clientes, notificador.novaFase(nomeJogadorAtual, fasesPorTurno.peek()));
    }

    /**
     * Posiciona tropas em um território do jogador.
     * @param jogadorId O jogador que realizou a requisição para posicionar as tropas.
     * @param territorioNome O nome do território do jogador que deve ser fortificado.
     * @param quantidadeTropas A quantidade de tropas a ser posicionada.
     *
     * */
    public void posicionarTropas(int jogadorId, String territorioNome, Integer quantidadeTropas) {
        validator.validarTerritorio(territorioNome, territorios);

        Jogador jogador = jogadores.get(buscarPosicaoJogador(jogadorId));
        Territorio territorio = territorios.get(territorioNome);

        validator.validarTurnoJogador(jogadores, jogadorId, jogadorAtualIndex);
        validator.validarFaseAtual(fasesPorTurno, FasesJogo.POSCIONAMENTO);
        validator.validarTerritorioJogador(territorio, jogador);
        validator.validarTropasDisponiveis(quantidadeTropas, jogador);

        territorio.adicionarTropas(quantidadeTropas);
        jogador.setTropasDisponiveis(jogador.getTropasDisponiveis()-quantidadeTropas);

        territorio.reorganizarTropas();

        notificador.callback(clientes, notificador.tropasAdicionadas(jogador.getNome(), territorioNome, territorio.getTotalTropas()));
    }

    /**
     * Calcula o bônus por cartas do jogador.
     * O jogador recebe 2 tropas sempre que tiver 3 cartas iguais para trocar.
     *
     * @param jogadorId O id do jogador que está iniciando um novo turno.
     * @return Uma mensagem indicando se houve troca de cartas (ou o motivo de não ter ocorrido a troca).
     * */
    public String calcularBonusCartas(int jogadorId) {
        Jogador jogador = jogadores.get(buscarPosicaoJogador(jogadorId));
        String mensagem;

        try{
            if (jogador.retirarCartasBonus(baralho)) {
                jogador.setTropasDisponiveis(jogador.getTropasDisponiveis()+2);
                mensagem = "Cartas Trocadas!";
            }else {
                mensagem = "Você não possui 3 cartas iguais para trocar!";
            }
        }catch (InvalidActionException e) {
            return e.getMessage();
        }

        return mensagem;
    }

    /**
     * Calcula a bonificação de territórios e continentes do jogador.
     * O total de tropas recebidas pelo jogador será:
     *      > a quantidade de territórios que ele possui, dividido por 3 (sendo sempre o mínimo de 3 tropas).
     *      > o valor de bônus do continente para cada continente que ele domina.
     *
     * @param jogadorId O id do jogador que está iniciando um novo turno.
     * */
    public void calcularBonificacao(int jogadorId) {
        Jogador jogador = jogadores.get(buscarPosicaoJogador(jogadorId));
        int bonusTerritorio, bonusContinente = 0;
        boolean pertenceAoJogador = true;

        bonusTerritorio = jogador.getTerritorios().size() / 3;
        if (bonusTerritorio < 3) bonusTerritorio = 3;

        for (Continente c : this.continentes) {
            for (Territorio t : c.getTerritorios()) {
                if (t.getDono() != jogador) {
                    pertenceAoJogador = false;
                    break;
                }
            }
            if (pertenceAoJogador) {
                bonusContinente += c.getBonus();
            }else {
                pertenceAoJogador = true;
            }
        }
        jogador.setTropasDisponiveis(jogador.getTropasDisponiveis()+bonusTerritorio+bonusContinente);
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
     * Verifica se já está no último jogador da fila.
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
        int totalTropas = territorio.getTotalTropas();

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
        int totalTropas = territorio.getTotalTropas();

        if (totalTropas >= 2) return 2;
        return 1;
    }

    public int buscarPosicaoJogador(int jogadorId) {
        int index = -1;

        for (int i = 0; i < jogadores.size(); i++) {
            Jogador j = jogadores.get(i);
            if (j.getId() == jogadorId) {
                index = i;
                break;
            }
        }

        return index;
    }

    /*******************************************************
     *                GETTERS E SETTERS
     *******************************************************/

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    /**
     * Cria uma string com todas as informações importantes do estado atual do jogo.
     * @return A string de estado do Jogo.
     * */
    public String exibirEstadoJogo() {
        String mensagem = "";

        mensagem = """
                =========================================================================
                                            Territórios
                =========================================================================                             
                """;

        for (String t : territorios.keySet()) {
            Territorio territorio = territorios.get(t);
            mensagem = mensagem + "Dono: " + territorio.getDono().getNome() + " " + territorio.getNome() + " | Vizinhos: " + territorio.getVizinhos() + " Tropas: " + territorio.exibirTropas(tropas) + "\n";
        }

        mensagem = mensagem + """
                =========================================================================
                                            Continentes
                =========================================================================                             
                """;

        for (Continente c : continentes) {
            mensagem = mensagem + "Nome: " + c.getNome() + " | Territórios: [" + c.getNomesTerritorios() + "] | Bonus: " + c.getBonus() + "\n";
        }

        mensagem = mensagem + """
                =========================================================================
                                            Jogadores
                =========================================================================                             
                """;

        for (Jogador j : jogadores) {
            mensagem = mensagem + "Nome: " + j.getNome() + " | Quantidade Territórios: " + j.getTerritorios().size() + " | Total Tropas: " + j.buscarTotalTropasJogador() + " | Tropas: \n" + j.buscarTerritoriosTropas() + "\n\n";
        }

        mensagem = mensagem + """
                =========================================================================
                                            GERAL                                        
                =========================================================================                             
                """;
        mensagem = mensagem + " Quantidade de cartas no Baralho: " + baralho.size() +
                   "\n Fase Atual: " + fasesPorTurno.peek() +
                   "\n Jogador Atual: " + jogadores.get(jogadorAtualIndex).getNome();

        return mensagem;
    }

    @Override
    public String toString() {
        return "GameManager{" +
                "territorios=" + territorios +
                ", jogadores=" + jogadores +
                ", continentes=" + continentes +
                ", fasesPorTurno=" + fasesPorTurno +
                ", ataquesResultados=" + ataquesResultados +
                ", defesasResultados=" + defesasResultados +
                ", tropas=" + tropas +
                ", baralho=" + baralho +
                ", jogadorAtualIndex=" + jogadorAtualIndex +
                ", TAMANHO_BARALHO=" + TAMANHO_BARALHO +
                ", dado=" + dado +
                ", jogoIniciado=" + jogoIniciado +
                ", MIN_JOGADORES=" + QUANTIDADE_JOGADORES +
                ", clientes=" + clientes +
                ", notificador=" + notificador +
                ", validator=" + validator +
                '}';
    }
}
