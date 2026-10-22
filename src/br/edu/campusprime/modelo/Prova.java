package br.edu.campusprime.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Prova {

    private String titulo;
    private String disciplina;
    private final List<Questao> questoes;

    public Prova(String titulo, String disciplina) {
        setTitulo(titulo);
        setDisciplina(disciplina);
        this.questoes = new ArrayList<>();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O título da prova não pode ficar vazio."
            );
        }

        this.titulo = titulo.trim();
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        if (disciplina == null || disciplina.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "A disciplina não pode ficar vazia."
            );
        }

        this.disciplina = disciplina.trim();
    }

    public void adicionarQuestao(Questao questao) {
        if (questao == null) {
            throw new IllegalArgumentException(
                "A questão não pode ser nula."
            );
        }

        questoes.add(questao);
    }

    public int contarQuestoes() {
        return questoes.size();
    }

    public double calcularValorTotal() {
        double total = 0.0;

        for (Questao questao : questoes) {
            total += questao.getValor();
        }

        return total;
    }

    public List<Questao> getQuestoes() {
        return Collections.unmodifiableList(questoes);
    }

    public String gerarResumo() {
        return "Prova: " + titulo
                + "\nDisciplina: " + disciplina
                + "\nQuantidade de questões: " + contarQuestoes()
                + "\nValor total: " + calcularValorTotal();
    }
}