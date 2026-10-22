package br.edu.campusprime.servico;

import br.edu.campusprime.modelo.Prova;
import br.edu.campusprime.modelo.Questao;

import java.util.List;

public class CorrecaoService {

    public double corrigirQuestao(
            Questao questao,
            String resposta,
            Double notaDiscursiva) {

        if (questao == null) {
            throw new IllegalArgumentException(
                "A questão não pode ser nula."
            );
        }

        return questao.corrigir(resposta, notaDiscursiva);
    }

    public double calcularNotaFinal(
            Prova prova,
            List<String> respostas,
            List<Double> notasDiscursivas) {

        if (prova == null || respostas == null
                || notasDiscursivas == null) {
            throw new IllegalArgumentException(
                "A prova e as listas de respostas são obrigatórias."
            );
        }

        int quantidadeQuestoes = prova.contarQuestoes();

        if (respostas.size() != quantidadeQuestoes
                || notasDiscursivas.size() != quantidadeQuestoes) {
            throw new IllegalArgumentException(
                "Deve existir uma resposta e uma nota por questão."
            );
        }

        double notaTotal = 0.0;
        List<Questao> questoes = prova.getQuestoes();

        for (int i = 0; i < questoes.size(); i++) {
            Questao questao = questoes.get(i);

            notaTotal += corrigirQuestao(
                questao,
                respostas.get(i),
                notasDiscursivas.get(i)
            );
        }

        return notaTotal;
    }
}