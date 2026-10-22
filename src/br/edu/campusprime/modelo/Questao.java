package br.edu.campusprime.modelo;

public abstract class Questao {

    private String enunciado;
    private double valor;

    public Questao(String enunciado, double valor) {
        setEnunciado(enunciado);
        setValor(valor);
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O enunciado da questão não pode ficar vazio."
            );
        }

        this.enunciado = enunciado.trim();
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(
                "O valor da questão deve ser maior que zero."
            );
        }

        this.valor = valor;
    }

    // Cada tipo de questão implementa sua própria correção.
    public abstract double corrigir(String resposta);

    // Permite informar uma nota manual quando necessário.
    // Por padrão, utiliza a correção normal da questão.
    public double corrigir(String resposta, Double notaManual) {
        return corrigir(resposta);
    }
}