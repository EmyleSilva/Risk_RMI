package com.RiskRmi.server;

import com.RiskRmi.enuns.FasesJogo;
import com.RiskRmi.enuns.Territorios;
import com.RiskRmi.enuns.TipoCarta;
import com.RiskRmi.enuns.TipoTropa;
import com.RiskRmi.model.*;

import java.util.*;

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
    private final int MIN_JOGADORES = 3;

    public GameManager(int TAMANHO_BARALHO) {
        this.TAMANHO_BARALHO = TAMANHO_BARALHO;
        this.dado = new Dado();
        this.jogadores = new ArrayList<>();

    }

    /**
     * Atualiza a flag de sinalização de inicio do jogo quando a quantidade mínima de jogadores
     * é antigida.
     * Além disso, inicia o processo de criação do jogo através da chamada do metodo criarJogo()
     * */
    public void verificarInicioJogo() {
        if (this.jogadores.size() >= MIN_JOGADORES && !jogoIniciado) {
            criarJogo();
            jogoIniciado = true;
            System.out.println("Jogo Iniciado!");
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
    public void criarJogo() {
          criarTropas();
          criarTerritorios();
          criarContinentes();
          criarBaralho();
          criarPilhaFases(true);

          distribuirTerritorios();

        /**Calcula a quantidade inicial de tropas de cada jogador */
        int totalTropas = calcularTropasIniciais(jogadores.size());
        for (Jogador j : jogadores) {
            j.setTropasDisponiveis(totalTropas - j.getTerritorios().size());
        }

        System.out.println("ID do Jogador Atual: " + jogadores.get(jogadorAtualIndex).getId());
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
     * Caso contrário, se trata de um turno normal, então empilha as fases de "POSICIONAMENTO", "ATAQUE" e "MOVIMENTAÇÃO".
     *
     * @param inicioJogo Flag que indica se é ou não inicio do jogo.
     * */
    public void criarPilhaFases(Boolean inicioJogo) {

        fasesPorTurno = new Stack<>();

        if (inicioJogo) {
            fasesPorTurno.push(FasesJogo.POSICIONAMETO_INICAL);
        }else {
            fasesPorTurno.push(FasesJogo.MOVIMENTACAO);
            fasesPorTurno.push(FasesJogo.ATAQUE);
            fasesPorTurno.push(FasesJogo.POSCIONAMENTO);
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
    public int calcularTropasIniciais(int quantidadeJogadores) {
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
     * Verifica se o jogo está em fase de aguardar jogador (quando o mínimo de
     * jogadores ainda não foi atingido)
     *
     * @return true quando está aguardando, false quando jogo é iniciado.
     * */
    Boolean verificarAguardandoJogadores() {
        return !jogoIniciado;
    }

    /*******************************************************
     *            MÉTODOS AUXILIARES
     *******************************************************/


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
