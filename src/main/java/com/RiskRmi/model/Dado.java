package com.RiskRmi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Dado implements Serializable {

    Random gerador = new Random();

    /**
     * Gera um número aleatório entre 1 e 6 para simular um dado.
     * @return O resultado do dado.
     * */
    public Integer rolar() {
        return gerador.nextInt(6) + 1;
    }

    /**
     * Faz o lançamento dos dados de ataque e defesa e os salva em um array de resultados.
     * @return Uma lista com os resultados dos lançamentos dos dados.
     * */
    public List<Integer> rolarDados(int quantidadeDados) {
        List<Integer> resultados = new ArrayList<>();

        for(int i = 0; i < quantidadeDados; i++) {
            resultados.add(rolar());
        }

        resultados.sort(Collections.reverseOrder());
        return resultados;

    }
}
