
package br.edu.campusprime.interfacegrafica;

import br.edu.campusprime.modelo.Prova;
import br.edu.campusprime.modelo.Questao;
import br.edu.campusprime.modelo.QuestaoDiscursiva;
import br.edu.campusprime.modelo.QuestaoMultiplaEscolha;
import br.edu.campusprime.repositorio.ProvaRepositorio;
import br.edu.campusprime.servico.CorrecaoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TelaCorrecao extends JFrame {

    private static final Color FUNDO = new Color(11, 25, 36);
    private static final Color PAINEL = new Color(21, 51, 66);
    private static final Color AZUL = new Color(56, 189, 248);
    private static final Color VERDE = new Color(45, 212, 191);
    private static final Color AMARELO = new Color(250, 204, 21);
    private static final Color BRANCO = Color.WHITE;
    private static final Color TEXTO_ESCURO = new Color(11, 25, 36);

    private final ProvaRepositorio repositorio = new ProvaRepositorio();
    private final CorrecaoService servico = new CorrecaoService();

    private final JComboBox<Prova> comboProvas = new JComboBox<>();
    private final JTextField campoAluno = new JTextField();
    private final JTextField campoTurma = new JTextField();

    private final JPanel painelQuestoes = new JPanel();
    private final List<JComponent> camposRespostas = new ArrayList<>();
    private final List<JTextField> camposNotas = new ArrayList<>();

    private Prova provaSelecionada;

    public TelaCorrecao() {
        setTitle("CampusPrime | Correção de Provas");
        setSize(1000, 800);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        criarInterface();
        carregarProvas();
    }

    private void criarInterface() {
        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBackground(FUNDO);
        principal.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titulo = new JLabel("Correção de Provas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(AZUL);

        JLabel subtitulo = new JLabel(
            "Identifique o aluno, registre as respostas e calcule a nota."
        );
        subtitulo.setForeground(BRANCO);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel cabecalho = new JPanel();
        cabecalho.setOpaque(false);
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(6));
        cabecalho.add(subtitulo);

        JPanel identificacao = new JPanel(new GridLayout(3, 2, 10, 10));
        identificacao.setBackground(PAINEL);
        identificacao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        identificacao.add(criarLabel("Prova cadastrada"));
        identificacao.add(comboProvas);
        identificacao.add(criarLabel("Nome completo do aluno"));
        identificacao.add(campoAluno);
        identificacao.add(criarLabel("Turma"));
        identificacao.add(campoTurma);

        comboProvas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        estilizarCampo(campoAluno);
        estilizarCampo(campoTurma);
        estilizarCombo(comboProvas);

        comboProvas.addActionListener(e -> carregarQuestoes());

        painelQuestoes.setLayout(
            new BoxLayout(painelQuestoes, BoxLayout.Y_AXIS)
        );
        painelQuestoes.setBackground(FUNDO);
        painelQuestoes.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane rolagem = new JScrollPane(painelQuestoes);
        rolagem.setBorder(BorderFactory.createLineBorder(
            new Color(45, 70, 85)
        ));
        rolagem.getViewport().setBackground(FUNDO);
        rolagem.getVerticalScrollBar().setUnitIncrement(16);

        JButton botaoCorrigir = criarBotao(
            "CALCULAR E SALVAR", VERDE, TEXTO_ESCURO
        );
        JButton botaoLimpar = criarBotao(
            "LIMPAR CAMPOS", AMARELO, TEXTO_ESCURO
        );
        JButton botaoFechar = criarBotao(
            "FECHAR", AZUL, TEXTO_ESCURO
        );

        botaoCorrigir.addActionListener(e -> corrigirESalvar());
        botaoLimpar.addActionListener(e -> limparCampos());
        botaoFechar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 5)
        );
        botoes.setOpaque(false);
        botoes.add(botaoLimpar);
        botoes.add(botaoFechar);
        botoes.add(botaoCorrigir);

        JPanel topo = new JPanel(new BorderLayout(10, 15));
        topo.setOpaque(false);
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(identificacao, BorderLayout.CENTER);

        principal.add(topo, BorderLayout.NORTH);
        principal.add(rolagem, BorderLayout.CENTER);
        principal.add(botoes, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(BRANCO);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBackground(new Color(235, 245, 250));
        campo.setForeground(Color.BLACK);
        campo.setCaretColor(Color.BLACK);
        campo.setOpaque(true);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL, 1),
            new EmptyBorder(7, 8, 7, 8)
        ));
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(new Color(235, 245, 250));
        combo.setForeground(TEXTO_ESCURO);
        combo.setOpaque(true);
    }

    private JButton criarBotao(String texto, Color cor, Color corTexto) {
        JButton botao = new JButton(texto);

        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setBackground(cor);
        botao.setForeground(corTexto);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(true);
        botao.setFocusPainted(false);
        botao.setRolloverEnabled(true);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor.brighter(), 2),
            new EmptyBorder(10, 14, 10, 14)
        ));

        botao.setPreferredSize(new Dimension(
            texto.equals("CALCULAR E SALVAR") ? 185 : 145,
            44
        ));

        botao.getModel().addChangeListener(e -> {
            ButtonModel modelo = botao.getModel();

            if (modelo.isPressed()) {
                botao.setBackground(cor.darker());
            } else if (modelo.isRollover()) {
                botao.setBackground(cor.brighter());
            } else {
                botao.setBackground(cor);
            }
        });

        return botao;
    }

    private void carregarProvas() {
        try {
            List<Prova> provas = repositorio.carregar();
            comboProvas.removeAllItems();

            for (Prova prova : provas) {
                comboProvas.addItem(prova);
            }

            if (provas.isEmpty()) {
                painelQuestoes.removeAll();

                JLabel aviso = criarLabel(
                    "Nenhuma prova cadastrada. Cadastre uma prova antes de corrigir."
                );

                painelQuestoes.add(aviso);
                painelQuestoes.revalidate();
                painelQuestoes.repaint();
            } else {
                carregarQuestoes();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Não foi possível carregar as provas.\n" + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void carregarQuestoes() {
        provaSelecionada = (Prova) comboProvas.getSelectedItem();

        painelQuestoes.removeAll();
        camposRespostas.clear();
        camposNotas.clear();

        if (provaSelecionada == null) {
            painelQuestoes.revalidate();
            painelQuestoes.repaint();
            return;
        }

        List<Questao> questoes = provaSelecionada.getQuestoes();

        if (questoes.isEmpty()) {
            painelQuestoes.add(criarLabel(
                "Esta prova ainda não possui questões."
            ));
        }

        for (int i = 0; i < questoes.size(); i++) {
            Questao questao = questoes.get(i);

            JPanel cartao = new JPanel();
            cartao.setLayout(
                new BoxLayout(cartao, BoxLayout.Y_AXIS)
            );
            cartao.setBackground(PAINEL);
            cartao.setAlignmentX(Component.LEFT_ALIGNMENT);
            cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    questao instanceof QuestaoDiscursiva ? VERDE : AZUL,
                    1
                ),
                new EmptyBorder(12, 12, 12, 12)
            ));

            JLabel enunciado = criarLabel(
                "Questão " + (i + 1) + " — Valor: "
                    + formatar(questao.getValor()) + " pontos"
            );
            enunciado.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextArea texto = new JTextArea(questao.getEnunciado());
            texto.setEditable(false);
            texto.setLineWrap(true);
            texto.setWrapStyleWord(true);
            texto.setOpaque(false);
            texto.setForeground(BRANCO);
            texto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            texto.setAlignmentX(Component.LEFT_ALIGNMENT);

            cartao.add(enunciado);
            cartao.add(Box.createVerticalStrut(8));
            cartao.add(texto);
            cartao.add(Box.createVerticalStrut(10));

            if (questao instanceof QuestaoMultiplaEscolha objetiva) {
                List<String> alternativas = objetiva.getAlternativas();

                String[] opcoes = new String[alternativas.size() + 1];
                opcoes[0] = "Selecione uma alternativa";

                for (int j = 0; j < alternativas.size(); j++) {
                    opcoes[j + 1] = (char) ('A' + j) + ") "
                        + alternativas.get(j);
                }

                JComboBox<String> resposta = new JComboBox<>(opcoes);
                estilizarCombo(resposta);
                resposta.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 35)
                );
                resposta.setAlignmentX(Component.LEFT_ALIGNMENT);

                cartao.add(resposta);
                camposRespostas.add(resposta);
                camposNotas.add(null);

            } else if (questao instanceof QuestaoDiscursiva) {
                JTextArea resposta = new JTextArea(4, 30);
                resposta.setLineWrap(true);
                resposta.setWrapStyleWord(true);
                resposta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                resposta.setBackground(new Color(235, 245, 250));
                resposta.setForeground(TEXTO_ESCURO);
                resposta.setCaretColor(TEXTO_ESCURO);
                resposta.setBorder(new EmptyBorder(6, 6, 6, 6));

                JScrollPane areaResposta = new JScrollPane(resposta);
                areaResposta.setAlignmentX(Component.LEFT_ALIGNMENT);
                areaResposta.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 100)
                );

                JTextField nota = new JTextField();
                estilizarCampo(nota);
                nota.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 35)
                );
                nota.setToolTipText(
                    "Informe a nota manual entre 0 e "
                        + formatar(questao.getValor())
                );

                JLabel labelNota = criarLabel(
                    "Nota manual da questão:"
                );
                labelNota.setAlignmentX(Component.LEFT_ALIGNMENT);

                cartao.add(areaResposta);
                cartao.add(Box.createVerticalStrut(8));
                cartao.add(labelNota);
                cartao.add(nota);

                camposRespostas.add(resposta);
                camposNotas.add(nota);
            }

            painelQuestoes.add(cartao);
            painelQuestoes.add(Box.createVerticalStrut(12));
        }

        painelQuestoes.revalidate();
        painelQuestoes.repaint();
    }

    private void corrigirESalvar() {
        if (provaSelecionada == null) {
            mostrarErro("Cadastre ou selecione uma prova primeiro.");
            return;
        }

        String aluno = campoAluno.getText().trim();
        String turma = campoTurma.getText().trim();

        if (aluno.isEmpty() || turma.isEmpty()) {
            mostrarErro(
                "Preencha o nome completo do aluno e a turma."
            );
            return;
        }

        List<Questao> questoes = provaSelecionada.getQuestoes();

        if (questoes.isEmpty()) {
            mostrarErro("A prova selecionada não possui questões.");
            return;
        }

        List<String> respostas = new ArrayList<>();
        List<Double> notasDiscursivas = new ArrayList<>();

        try {
            for (int i = 0; i < questoes.size(); i++) {
                Questao questao = questoes.get(i);
                JComponent campo = camposRespostas.get(i);

                if (questao instanceof QuestaoMultiplaEscolha) {
                    @SuppressWarnings("unchecked")
                    JComboBox<String> combo =
                        (JComboBox<String>) campo;

                    if (combo.getSelectedIndex() == 0) {
                        throw new IllegalArgumentException(
                            "Selecione uma alternativa na questão "
                                + (i + 1) + "."
                        );
                    }

                    respostas.add(
                        String.valueOf(combo.getSelectedIndex() - 1)
                    );
                    notasDiscursivas.add(null);

                } else if (questao instanceof QuestaoDiscursiva) {
                    JTextArea area = (JTextArea) campo;
                    String resposta = area.getText().trim();

                    if (resposta.isEmpty()) {
                        throw new IllegalArgumentException(
                            "Preencha a resposta da questão "
                                + (i + 1) + "."
                        );
                    }

                    JTextField campoNota = camposNotas.get(i);
                    String textoNota = campoNota.getText().trim();

                    if (textoNota.isEmpty()) {
                        throw new IllegalArgumentException(
                            "Informe a nota manual da questão "
                                + (i + 1) + "."
                        );
                    }

                    double nota = Double.parseDouble(
                        textoNota.replace(",", ".")
                    );

                    if (!Double.isFinite(nota)
                            || nota < 0
                            || nota > questao.getValor()) {
                        throw new IllegalArgumentException(
                            "A nota da questão " + (i + 1)
                                + " deve ficar entre 0 e "
                                + formatar(questao.getValor()) + "."
                        );
                    }

                    respostas.add(resposta);
                    notasDiscursivas.add(nota);
                }
            }

            double notaFinal = servico.calcularNotaFinal(
                provaSelecionada,
                respostas,
                notasDiscursivas
            );

            double valorTotal = provaSelecionada.calcularValorTotal();

            salvarHistorico(
                aluno,
                turma,
                provaSelecionada,
                respostas,
                notasDiscursivas,
                notaFinal,
                valorTotal
            );

            JOptionPane.showMessageDialog(
                this,
                "Correção salva com sucesso!\n\n"
                    + "Aluno: " + aluno
                    + "\nTurma: " + turma
                    + "\nProva: " + provaSelecionada.getTitulo()
                    + "\nNota obtida: " + formatar(notaFinal)
                    + "\nValor total: " + formatar(valorTotal),
                "Resultado da correção",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException ex) {
            mostrarErro(
                "Digite uma nota numérica válida. Exemplo: 1,5."
            );
        } catch (IllegalArgumentException ex) {
            mostrarErro(ex.getMessage());
        } catch (IOException ex) {
            mostrarErro(
                "Não foi possível salvar o histórico.\n"
                    + ex.getMessage()
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarErro(
                "Ocorreu um erro durante a correção: "
                    + ex.getMessage()
            );
        }
    }

    private void salvarHistorico(
            String aluno,
            String turma,
            Prova prova,
            List<String> respostas,
            List<Double> notasDiscursivas,
            double notaFinal,
            double valorTotal) throws IOException {

        Path arquivo = Paths.get("dados", "correcoes.json");
        Files.createDirectories(arquivo.getParent());

        String existente = Files.exists(arquivo)
            ? Files.readString(arquivo, StandardCharsets.UTF_8).trim()
            : "";

        if (existente.isEmpty()) {
            existente = "[]";
        }

        if (!existente.startsWith("[") || !existente.endsWith("]")) {
            throw new IOException(
                "O arquivo correcoes.json está inválido. "
                    + "Faça uma cópia de segurança antes de corrigir."
            );
        }

        String registro = criarRegistroJson(
            aluno,
            turma,
            prova,
            respostas,
            notasDiscursivas,
            notaFinal,
            valorTotal
        );

        String interior = existente
            .substring(1, existente.length() - 1).trim();

        String conteudo;

        if (interior.isEmpty()) {
            conteudo = "[\n" + registro + "\n]";
        } else {
            conteudo = "[\n" + interior + ",\n" + registro + "\n]";
        }

        Files.writeString(
            arquivo,
            conteudo,
            StandardCharsets.UTF_8
        );
    }

    private String criarRegistroJson(
            String aluno,
            String turma,
            Prova prova,
            List<String> respostas,
            List<Double> notasDiscursivas,
            double notaFinal,
            double valorTotal) {

        StringBuilder json = new StringBuilder();

        json.append("  {\n");
        json.append("    \"aluno\": \"")
            .append(escapar(aluno)).append("\",\n");
        json.append("    \"turma\": \"")
            .append(escapar(turma)).append("\",\n");
        json.append("    \"prova\": \"")
            .append(escapar(prova.getTitulo())).append("\",\n");
        json.append("    \"disciplina\": \"")
            .append(escapar(prova.getDisciplina())).append("\",\n");
        json.append("    \"dataHora\": \"")
            .append(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            )).append("\",\n");
        json.append("    \"notaFinal\": ").append(notaFinal).append(",\n");
        json.append("    \"valorTotal\": ").append(valorTotal).append(",\n");
        json.append("    \"respostas\": [");

        for (int i = 0; i < respostas.size(); i++) {
            if (i > 0) {
                json.append(", ");
            }

            json.append("\"")
                .append(escapar(respostas.get(i)))
                .append("\"");
        }

        json.append("],\n");
        json.append("    \"notasDiscursivas\": [");

        for (int i = 0; i < notasDiscursivas.size(); i++) {
            if (i > 0) {
                json.append(", ");
            }

            Double nota = notasDiscursivas.get(i);
            json.append(nota == null ? "null" : nota.toString());
        }

        json.append("]\n");
        json.append("  }");

        return json.toString();
    }

    private String escapar(String texto) {
        return texto.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    private String formatar(double valor) {
        return String.format(
            Locale.forLanguageTag("pt-BR"),
            "%.2f",
            valor
        );
    }

    private void limparCampos() {
        campoAluno.setText("");
        campoTurma.setText("");
        carregarQuestoes();
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
            this,
            mensagem,
            "CampusPrime | Atenção",
            JOptionPane.WARNING_MESSAGE
        );
    }
}
