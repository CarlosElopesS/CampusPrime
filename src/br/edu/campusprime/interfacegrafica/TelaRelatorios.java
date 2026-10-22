
package br.edu.campusprime.interfacegrafica;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelaRelatorios extends JFrame {

    private static final Color FUNDO = new Color(11, 25, 36);
    private static final Color PAINEL = new Color(21, 51, 66);
    private static final Color AZUL = new Color(56, 189, 248);
    private static final Color VERDE = new Color(45, 212, 191);
    private static final Color AMARELO = new Color(250, 204, 21);
    private static final Color BRANCO = Color.WHITE;
    private static final Color TEXTO_ESCURO = new Color(11, 25, 36);

    private final Path arquivo = Paths.get("dados", "correcoes.json");

    private final DefaultTableModel modelo = new DefaultTableModel(
        new Object[]{
            "Aluno", "Turma", "Prova", "Disciplina",
            "Data e hora", "Nota", "Valor total"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    private final JTable tabela = new JTable(modelo);
    private final JTextField campoPesquisa = new JTextField();

    private final JLabel totalCorrecoes = criarValorResumo("0");
    private final JLabel mediaNotas = criarValorResumo("0,00");
    private final JLabel melhorNota = criarValorResumo("0,00");

    private final List<String> registrosCarregados = new ArrayList<>();

    private TableRowSorter<DefaultTableModel> ordenador;

    public TelaRelatorios() {
        setTitle("CampusPrime | Relatórios");
        setSize(1150, 750);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        criarInterface();
        carregarRelatorios();
    }

    private void criarInterface() {
        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBackground(FUNDO);
        principal.setBorder(new EmptyBorder(22, 25, 22, 25));

        JLabel titulo = new JLabel("Relatórios de Correções");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(AZUL);

        JLabel subtitulo = new JLabel(
            "Consulte os resultados, as notas e o histórico dos alunos."
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(BRANCO);

        JPanel cabecalho = new JPanel();
        cabecalho.setOpaque(false);
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(6));
        cabecalho.add(subtitulo);

        JPanel resumo = new JPanel(new GridLayout(1, 3, 12, 0));
        resumo.setOpaque(false);
        resumo.add(criarCartaoResumo(
            "CORREÇÕES REGISTRADAS", totalCorrecoes, AZUL
        ));
        resumo.add(criarCartaoResumo(
            "MÉDIA DAS NOTAS", mediaNotas, VERDE
        ));
        resumo.add(criarCartaoResumo(
            "MAIOR NOTA", melhorNota, AMARELO
        ));

        JPanel topo = new JPanel(new BorderLayout(12, 15));
        topo.setOpaque(false);
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(resumo, BorderLayout.CENTER);

        JLabel labelPesquisa = new JLabel("Pesquisar:");
        labelPesquisa.setForeground(BRANCO);
        labelPesquisa.setFont(new Font("Segoe UI", Font.BOLD, 13));

        campoPesquisa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoPesquisa.setBackground(new Color(235, 245, 250));
        campoPesquisa.setForeground(TEXTO_ESCURO);
        campoPesquisa.setCaretColor(TEXTO_ESCURO);
        campoPesquisa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL),
            new EmptyBorder(8, 10, 8, 10)
        ));

        JPanel painelPesquisa = new JPanel(new BorderLayout(10, 0));
        painelPesquisa.setOpaque(false);
        painelPesquisa.add(labelPesquisa, BorderLayout.WEST);
        painelPesquisa.add(campoPesquisa, BorderLayout.CENTER);

        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(30);
        tabela.setBackground(new Color(235, 245, 250));
        tabela.setForeground(TEXTO_ESCURO);
        tabela.setSelectionBackground(AZUL);
        tabela.setSelectionForeground(TEXTO_ESCURO);
        tabela.setGridColor(new Color(205, 220, 230));
        tabela.setShowGrid(true);
        tabela.setFillsViewportHeight(true);

        tabela.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 12)
        );
        tabela.getTableHeader().setBackground(PAINEL);
        tabela.getTableHeader().setForeground(BRANCO);
        tabela.getTableHeader().setReorderingAllowed(false);

        ordenador = new TableRowSorter<>(modelo);
        tabela.setRowSorter(ordenador);

        campoPesquisa.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {
                private void filtrar() {
                    String texto = campoPesquisa.getText().trim();

                    if (texto.isEmpty()) {
                        ordenador.setRowFilter(null);
                    } else {
                        ordenador.setRowFilter(
                            RowFilter.regexFilter(
                                "(?i)" + Pattern.quote(texto)
                            )
                        );
                    }
                }

                @Override
                public void insertUpdate(
                        javax.swing.event.DocumentEvent e) {
                    filtrar();
                }

                @Override
                public void removeUpdate(
                        javax.swing.event.DocumentEvent e) {
                    filtrar();
                }

                @Override
                public void changedUpdate(
                        javax.swing.event.DocumentEvent e) {
                    filtrar();
                }
            }
        );

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(
            new Color(45, 70, 85)
        ));
        rolagem.getViewport().setBackground(new Color(235, 245, 250));

        JPanel painelTabela = new JPanel(new BorderLayout(0, 12));
        painelTabela.setOpaque(false);
        painelTabela.add(painelPesquisa, BorderLayout.NORTH);
        painelTabela.add(rolagem, BorderLayout.CENTER);

        JButton botaoDetalhes = criarBotao("VER DETALHES", VERDE);
        JButton botaoAtualizar = criarBotao("ATUALIZAR", AZUL);
        JButton botaoFechar = criarBotao("FECHAR", AMARELO);

        botaoDetalhes.addActionListener(e -> exibirDetalhes());
        botaoAtualizar.addActionListener(e -> carregarRelatorios());
        botaoFechar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 5)
        );
        botoes.setOpaque(false);
        botoes.add(botaoDetalhes);
        botoes.add(botaoAtualizar);
        botoes.add(botaoFechar);
        botoes.setPreferredSize(new Dimension(700, 55));

        principal.add(topo, BorderLayout.NORTH);
        principal.add(painelTabela, BorderLayout.CENTER);
        principal.add(botoes, BorderLayout.SOUTH);

        principal.revalidate();
        principal.repaint();

        setContentPane(principal);
    }

    private JPanel criarCartaoResumo(
            String titulo, JLabel valor, Color cor) {

        JPanel cartao = new JPanel();
        cartao.setLayout(new BoxLayout(cartao, BoxLayout.Y_AXIS));
        cartao.setBackground(PAINEL);
        cartao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor, 2),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel label = new JLabel(titulo);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(BRANCO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        valor.setForeground(cor);
        valor.setAlignmentX(Component.LEFT_ALIGNMENT);

        cartao.add(label);
        cartao.add(Box.createVerticalStrut(10));
        cartao.add(valor);

        return cartao;
    }

    private JLabel criarValorResumo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 25));
        return label;
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setBackground(cor);
        botao.setForeground(TEXTO_ESCURO);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(true);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor.brighter(), 2),
            new EmptyBorder(9, 14, 9, 14)
        ));
        return botao;
    }

    private void carregarRelatorios() {
        modelo.setRowCount(0);
        registrosCarregados.clear();

        List<Double> notas = new ArrayList<>();

        try {
            if (!Files.exists(arquivo)) {
                atualizarResumo(notas);
                return;
            }

            String conteudo = Files.readString(
                arquivo, StandardCharsets.UTF_8
            ).trim();

            if (conteudo.isEmpty() || conteudo.equals("[]")) {
                atualizarResumo(notas);
                return;
            }

            if (!conteudo.startsWith("[") || !conteudo.endsWith("]")) {
                throw new IOException(
                    "O arquivo JSON não possui uma estrutura válida."
                );
            }

            Pattern padraoRegistro = Pattern.compile("\\{([^{}]*)\\}");
            Matcher registros = padraoRegistro.matcher(conteudo);

            while (registros.find()) {
                String registro = registros.group(1);
                registrosCarregados.add(registro);

                String aluno = lerTexto(registro, "aluno");
                String turma = lerTexto(registro, "turma");
                String prova = lerTexto(registro, "prova");
                String disciplina = lerTexto(registro, "disciplina");
                String dataHora = lerTexto(registro, "dataHora");

                double nota = lerNumero(registro, "notaFinal");
                double total = lerNumero(registro, "valorTotal");

                modelo.addRow(new Object[]{
                    aluno, turma, prova, disciplina,
                    dataHora, formatar(nota), formatar(total)
                });

                notas.add(nota);
            }

            atualizarResumo(notas);

        } catch (Exception ex) {
            atualizarResumo(notas);

            JOptionPane.showMessageDialog(
                this,
                "Não foi possível carregar os relatórios.\n"
                    + ex.getMessage(),
                "CampusPrime | Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void exibirDetalhes() {
        int linhaSelecionada = tabela.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecione uma correção na tabela para ver os detalhes.",
                "CampusPrime | Detalhes",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        int indiceModelo = tabela.convertRowIndexToModel(linhaSelecionada);

        if (indiceModelo < 0
                || indiceModelo >= registrosCarregados.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Não foi possível localizar os dados dessa correção.",
                "CampusPrime | Erro",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String registro = registrosCarregados.get(indiceModelo);
        StringBuilder detalhes = new StringBuilder();

        detalhes.append("ALUNO: ")
            .append(lerTexto(registro, "aluno")).append("\n");
        detalhes.append("TURMA: ")
            .append(lerTexto(registro, "turma")).append("\n");
        detalhes.append("PROVA: ")
            .append(lerTexto(registro, "prova")).append("\n");
        detalhes.append("DISCIPLINA: ")
            .append(lerTexto(registro, "disciplina")).append("\n");
        detalhes.append("DATA E HORA: ")
            .append(lerTexto(registro, "dataHora")).append("\n");
        detalhes.append("NOTA FINAL: ")
            .append(formatar(lerNumero(registro, "notaFinal")))
            .append(" / ")
            .append(formatar(lerNumero(registro, "valorTotal")))
            .append("\n\n");

        detalhes.append("RESPOSTAS E NOTAS POR QUESTÃO\n");
        detalhes.append("--------------------------------\n");

        List<String> respostas = lerArray(registro, "respostas");
        List<String> notasDiscursivas =
            lerArray(registro, "notasDiscursivas");

        if (respostas.isEmpty() && notasDiscursivas.isEmpty()) {
            detalhes.append(
                "Não foram encontradas respostas detalhadas neste registro.\n"
            );
        } else {
            int quantidade = Math.max(
                respostas.size(), notasDiscursivas.size()
            );

            for (int i = 0; i < quantidade; i++) {
                detalhes.append("\nQuestão ")
                    .append(i + 1).append("\n");

                if (i < respostas.size()) {
                    detalhes.append("Resposta registrada: ")
                        .append(respostas.get(i) == null
                            ? "Sem resposta"
                            : respostas.get(i))
                        .append("\n");
                }

                if (i < notasDiscursivas.size()) {
                    String nota = notasDiscursivas.get(i);

                    if (nota != null && !nota.isBlank()) {
                        detalhes.append("Nota discursiva: ")
                            .append(nota).append("\n");
                    }
                }
            }
        }

        JTextArea area = new JTextArea(detalhes.toString());
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setBackground(new Color(235, 245, 250));
        area.setForeground(TEXTO_ESCURO);
        area.setCaretPosition(0);
        area.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane rolagem = new JScrollPane(area);
        rolagem.setPreferredSize(new Dimension(520, 400));

        JOptionPane.showMessageDialog(
            this,
            rolagem,
            "CampusPrime | Detalhes da Correção",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private List<String> lerArray(String registro, String chave) {
        List<String> valores = new ArrayList<>();

        Pattern padraoCampo = Pattern.compile(
            "\"" + Pattern.quote(chave) + "\"\\s*:\\s*\\[([^\\]]*)\\]"
        );

        Matcher campo = padraoCampo.matcher(registro);

        if (!campo.find()) {
            return valores;
        }

        String conteudo = campo.group(1);

        Pattern item = Pattern.compile(
            "\"((?:\\\\.|[^\"\\\\])*)\"|\\bnull\\b"
        );

        Matcher itens = item.matcher(conteudo);

        while (itens.find()) {
            if (itens.group(1) != null) {
                valores.add(desescapar(itens.group(1)));
            } else {
                valores.add(null);
            }
        }

        return valores;
    }

    private String lerTexto(String registro, String chave) {
        Pattern padrao = Pattern.compile(
            "\"" + Pattern.quote(chave)
                + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\""
        );

        Matcher matcher = padrao.matcher(registro);

        if (matcher.find()) {
            return desescapar(matcher.group(1));
        }

        return "";
    }

    private double lerNumero(String registro, String chave) {
        Pattern padrao = Pattern.compile(
            "\"" + Pattern.quote(chave)
                + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?)"
        );

        Matcher matcher = padrao.matcher(registro);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }

        return 0.0;
    }

    private String desescapar(String texto) {
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < texto.length(); i++) {
            char atual = texto.charAt(i);

            if (atual != '\\' || i + 1 >= texto.length()) {
                resultado.append(atual);
                continue;
            }

            char proximo = texto.charAt(++i);

            switch (proximo) {
                case '"' -> resultado.append('"');
                case '\\' -> resultado.append('\\');
                case '/' -> resultado.append('/');
                case 'n' -> resultado.append('\n');
                case 'r' -> resultado.append('\r');
                case 't' -> resultado.append('\t');
                case 'b' -> resultado.append('\b');
                case 'f' -> resultado.append('\f');

                case 'u' -> {
                    if (i + 4 < texto.length()) {
                        String hexadecimal = texto.substring(i + 1, i + 5);

                        try {
                            resultado.append(
                                (char) Integer.parseInt(hexadecimal, 16)
                            );
                            i += 4;
                        } catch (NumberFormatException ex) {
                            resultado.append("\\u");
                        }
                    } else {
                        resultado.append("\\u");
                    }
                }

                default -> resultado.append(proximo);
            }
        }

        return resultado.toString();
    }

    private void atualizarResumo(List<Double> notas) {
        int quantidade = notas.size();
        double soma = 0.0;
        double maior = 0.0;

        for (int i = 0; i < notas.size(); i++) {
            double nota = notas.get(i);
            soma += nota;

            if (i == 0 || nota > maior) {
                maior = nota;
            }
        }

        double media = quantidade > 0
            ? soma / quantidade
            : 0.0;

        totalCorrecoes.setText(String.valueOf(quantidade));
        mediaNotas.setText(formatar(media));
        melhorNota.setText(formatar(maior));
    }

    private String formatar(double valor) {
        return String.format(
            Locale.forLanguageTag("pt-BR"),
            "%.2f",
            valor
        );
    }
}
