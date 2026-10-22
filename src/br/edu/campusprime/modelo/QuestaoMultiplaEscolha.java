
package br.edu.campusprime.modelo;

import java.util.ArrayList;
import java.util.List;

public class QuestaoMultiplaEscolha extends Questao {

    private List<String> alternativas;
    private int indiceCorreto;

    public QuestaoMultiplaEscolha(
            String enunciado,
            double valor,
            List<String> alternativas,
            int indiceCorreto) {

        super(enunciado, valor);
        setAlternativas(alternativas);
        setIndiceCorreto(indiceCorreto);
    }

    public List<String> getAlternativas() {
        return new ArrayList<>(alternativas);
    }

    public void setAlternativas(List<String> alternativas) {
        if (alternativas == null || alternativas.size() < 2) {
            throw new IllegalArgumentException(
                "A questão deve ter pelo menos duas alternativas."
            );
        }

        List<String> copia = new ArrayList<>();

        for (String alternativa : alternativas) {
            if (alternativa == null || alternativa.trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "As alternativas não podem ficar vazias."
                );
            }

            copia.add(alternativa.trim());
        }

        this.alternativas = copia;

        if (indiceCorreto < 0 || indiceCorreto >= copia.size()) {
            indiceCorreto = -1;
        }
    }

    public int getIndiceCorreto() {
        return indiceCorreto;
    }

    public void setIndiceCorreto(int indiceCorreto) {
        if (alternativas == null
                || indiceCorreto < 0
                || indiceCorreto >= alternativas.size()) {
            throw new IllegalArgumentException(
                "O índice da alternativa correta é inválido."
            );
        }

        this.indiceCorreto = indiceCorreto;
    }

    @Override
    public double corrigir(String resposta) {
        if (resposta == null || resposta.trim().isEmpty()) {
            return 0.0;
        }

        try {
            int indiceResposta = Integer.parseInt(resposta.trim());

            if (indiceResposta == indiceCorreto) {
                return getValor();
            }
        } catch (NumberFormatException e) {
            // Uma resposta que não seja um número é considerada incorreta.
        }

        return 0.0;
    }
}

