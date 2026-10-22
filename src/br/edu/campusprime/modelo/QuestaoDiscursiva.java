package br.edu.campusprime.modelo;

public class QuestaoDiscursiva extends Questao {

    private String respostaEsperada;

    public QuestaoDiscursiva(
            String enunciado,
            double valor,
            String respostaEsperada) {

        super(enunciado, valor);
        setRespostaEsperada(respostaEsperada);
    }

    public String getRespostaEsperada() {
        return respostaEsperada;
    }

    public void setRespostaEsperada(String respostaEsperada) {
        if (respostaEsperada == null
                || respostaEsperada.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "A resposta esperada não pode ficar vazia."
            );
        }

        this.respostaEsperada = respostaEsperada.trim();
    }

    @Override
    public double corrigir(String resposta) {
        // A questão discursiva precisa de avaliação manual.
        return 0.0;
    }

    @Override
    public double corrigir(String resposta, Double notaManual) {
        if (notaManual == null) {
            throw new IllegalArgumentException(
                "Informe a nota da questão discursiva."
            );
        }

        if (!Double.isFinite(notaManual)
                || notaManual < 0
                || notaManual > getValor()) {
            throw new IllegalArgumentException(
                "A nota deve estar entre zero e o valor da questão."
            );
        }

        return notaManual;
    }
}