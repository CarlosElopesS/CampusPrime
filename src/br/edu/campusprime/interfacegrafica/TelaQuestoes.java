
package br.edu.campusprime.interfacegrafica;

import br.edu.campusprime.modelo.Prova;
import br.edu.campusprime.modelo.Questao;
import br.edu.campusprime.modelo.QuestaoDiscursiva;
import br.edu.campusprime.modelo.QuestaoMultiplaEscolha;
import br.edu.campusprime.repositorio.ProvaRepositorio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TelaQuestoes extends JFrame {

    private static final Color FUNDO = new Color(11, 25, 36);
    private static final Color PAINEL = new Color(21, 51, 66);
    private static final Color AZUL = new Color(56, 189, 248);
    private static final Color VERDE = new Color(45, 212, 191);
    private static final Color BRANCO = Color.WHITE;

    private final ProvaRepositorio repositorio = new ProvaRepositorio();

    private final JComboBox<Prova> comboProvas = new JComboBox<>();
    private final JComboBox<String> comboTipo = new JComboBox<>(
        new String[]{"Múltipla escolha", "Discursiva"}
    );

    private final JTextArea campoEnunciado = new JTextArea(4, 30);
    private final JTextField campoValor = new JTextField();
    private final JTextField[] alternativas = new JTextField[4];

    private final JComboBox<String> comboCorreta = new JComboBox<>(
        new String[]{"Alternativa A", "Alternativa B",
                     "Alternativa C", "Alternativa D"}
    );

    private final JTextField campoRespostaEsperada = new JTextField();
    private final DefaultListModel<String> modeloQuestoes =
        new DefaultListModel<>();
    private final JList<String> listaQuestoes = new JList<>(modeloQuestoes);

    private final JPanel painelAlternativas = new JPanel();
    private final JPanel painelDiscursiva = new JPanel();

    private List<Prova> provas = new ArrayList<>();

    public TelaQuestoes() {
        setTitle("CampusPrime | Questões");
        setSize(900, 750);
        setMinimumSize(new Dimension(750, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        criarInterface();
        carregarProvas();

        comboProvas.addActionListener(e -> atualizarListaQuestoes());
        comboTipo.addActionListener(e -> atualizarTipoQuestao());

        atualizarTipoQuestao();
    }

    private void criarInterface() {
        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBackground(FUNDO);
        principal.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titulo = new JLabel("Cadastro de Questões");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(AZUL);

        JLabel subtitulo = new JLabel(
            "Adicione questões às avaliações cadastradas"
        );
        subtitulo.setForeground(BRANCO);

        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);
        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(5));
        cabecalho.add(subtitulo);

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBackground(PAINEL);
        formulario.setBorder(new EmptyBorder(15, 15, 15, 15));

        campoEnunciado.setBackground(new Color(240, 245, 248));
        campoEnunciado.setForeground(FUNDO);
        campoEnunciado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoEnunciado.setLineWrap(true);
        campoEnunciado.setWrapStyleWord(true);

        adicionarCampo(formulario, "Prova:", comboProvas);
        adicionarCampo(formulario, "Tipo de questão:", comboTipo);
        adicionarCampo(formulario, "Enunciado:", new JScrollPane(campoEnunciado));
        adicionarCampo(formulario, "Valor da questão:", campoValor);

        painelAlternativas.setLayout(
            new BoxLayout(painelAlternativas, BoxLayout.Y_AXIS)
        );
        painelAlternativas.setBackground(PAINEL);

        String[] letras = {"A", "B", "C", "D"};

        for (int i = 0; i < alternativas.length; i++) {
            alternativas[i] = new JTextField();
            configurarCampo(alternativas[i]);

            adicionarCampo(
                painelAlternativas,
                "Alternativa " + letras[i] + ":",
                alternativas[i]
            );
        }

        adicionarCampo(
            painelAlternativas,
            "Resposta correta:",
            comboCorreta
        );

        painelDiscursiva.setLayout(
            new BoxLayout(painelDiscursiva, BoxLayout.Y_AXIS)
        );
        painelDiscursiva.setBackground(PAINEL);

        configurarCampo(campoRespostaEsperada);

        adicionarCampo(
            painelDiscursiva,
            "Resposta esperada (para correção):",
            campoRespostaEsperada
        );

        JButton botaoSalvar = new JButton("Salvar questão");
        botaoSalvar.setBackground(VERDE);
        botaoSalvar.setForeground(FUNDO);
        botaoSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botaoSalvar.setFocusPainted(false);
        botaoSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoSalvar.addActionListener(e -> salvarQuestao());

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotao.setOpaque(false);
        painelBotao.add(botaoSalvar);

        listaQuestoes.setBackground(PAINEL);
        listaQuestoes.setForeground(BRANCO);
        listaQuestoes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaQuestoes.setSelectionBackground(AZUL);
        listaQuestoes.setSelectionForeground(FUNDO);

        JLabel tituloLista = new JLabel("Questões da prova selecionada");
        tituloLista.setForeground(AZUL);
        tituloLista.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel painelLista = new JPanel(new BorderLayout(5, 8));
        painelLista.setOpaque(false);
        painelLista.add(tituloLista, BorderLayout.NORTH);
        painelLista.add(
            new JScrollPane(listaQuestoes),
            BorderLayout.CENTER
        );

        JPanel centro = new JPanel(new BorderLayout(15, 15));
        centro.setOpaque(false);
        centro.add(formulario, BorderLayout.CENTER);
        centro.add(painelLista, BorderLayout.SOUTH);

        principal.add(cabecalho, BorderLayout.NORTH);
        principal.add(centro, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.add(painelBotao, BorderLayout.EAST);

        principal.add(rodape, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private void adicionarCampo(
            JPanel painel, String texto, JComponent componente) {

        JLabel label = new JLabel(texto);
        label.setForeground(BRANCO);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        componente.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, componente instanceof JScrollPane
                ? 100 : 32)
        );

        painel.add(label);
        painel.add(Box.createVerticalStrut(4));
        painel.add(componente);
        painel.add(Box.createVerticalStrut(8));
    }

    private void configurarCampo(JTextField campo) {
        campo.setBackground(new Color(240, 245, 248));
        campo.setForeground(FUNDO);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void carregarProvas() {
        try {
            provas = repositorio.carregar();

            comboProvas.removeAllItems();

            for (Prova prova : provas) {
                comboProvas.addItem(prova);
            }

            comboProvas.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> lista, Object valor, int indice,
                        boolean selecionado, boolean foco) {

                    super.getListCellRendererComponent(
                        lista, valor, indice, selecionado, foco
                    );

                    if (valor instanceof Prova prova) {
                        setText(
                            prova.getTitulo() + " — " + prova.getDisciplina()
                        );
                    }

                    return this;
                }
            });

            atualizarListaQuestoes();

            if (provas.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Cadastre uma prova antes de adicionar questões.",
                    "Nenhuma prova encontrada",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Não foi possível carregar as provas: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void atualizarTipoQuestao() {
        boolean multipla =
            comboTipo.getSelectedIndex() == 0;

        painelAlternativas.setVisible(multipla);
        painelDiscursiva.setVisible(!multipla);

        // Os campos são adicionados ao formulário principal.
        // Atualiza a interface após a troca do tipo.
        Container pai = campoValor.getParent();

        if (pai != null) {
            pai.revalidate();
            pai.repaint();
        }
    }

    private void salvarQuestao() {
        Prova prova = (Prova) comboProvas.getSelectedItem();

        if (prova == null) {
            mostrarErro("Cadastre e selecione uma prova primeiro.");
            return;
        }

        String enunciado = campoEnunciado.getText().trim();

        if (enunciado.isEmpty()) {
            mostrarErro("Informe o enunciado da questão.");
            return;
        }

        double valor;

        try {
            valor = Double.parseDouble(
                campoValor.getText().trim().replace(",", ".")
            );

            if (!Double.isFinite(valor) || valor <= 0) {
                mostrarErro("O valor deve ser um número maior que zero.");
                return;
            }
        } catch (NumberFormatException ex) {
            mostrarErro("Informe um valor numérico válido.");
            return;
        }

        try {
            Questao questao;

            if (comboTipo.getSelectedIndex() == 0) {
                List<String> opcoes = new ArrayList<>();

                for (JTextField alternativa : alternativas) {
                    String texto = alternativa.getText().trim();

                    if (texto.isEmpty()) {
                        mostrarErro("Preencha as quatro alternativas.");
                        return;
                    }

                    opcoes.add(texto);
                }

                questao = new QuestaoMultiplaEscolha(
                    enunciado,
                    valor,
                    opcoes,
                    comboCorreta.getSelectedIndex()
                );
            } else {
                String esperada =
                    campoRespostaEsperada.getText().trim();

                if (esperada.isEmpty()) {
                    mostrarErro("Informe a resposta esperada.");
                    return;
                }

                questao = new QuestaoDiscursiva(
                    enunciado,
                    valor,
                    esperada
                );
            }

            prova.adicionarQuestao(questao);
            repositorio.atualizarTodas(provas);

            atualizarListaQuestoes();
            limparCampos();

            JOptionPane.showMessageDialog(
                this,
                "Questão cadastrada e salva com sucesso!",
                "CampusPrime",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Não foi possível salvar a questão: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void atualizarListaQuestoes() {
        modeloQuestoes.clear();

        Prova prova = (Prova) comboProvas.getSelectedItem();

        if (prova == null) {
            return;
        }

        int numero = 1;

        for (Questao questao : prova.getQuestoes()) {
            String tipo = questao instanceof QuestaoMultiplaEscolha
                ? "Múltipla escolha" : "Discursiva";

            modeloQuestoes.addElement(
                numero + ". [" + tipo + "] "
                    + questao.getEnunciado()
                    + " — " + questao.getValor() + " ponto(s)"
            );

            numero++;
        }
    }

    private void limparCampos() {
        campoEnunciado.setText("");
        campoValor.setText("");

        for (JTextField alternativa : alternativas) {
            alternativa.setText("");
        }

        campoRespostaEsperada.setText("");
        comboCorreta.setSelectedIndex(0);
        campoEnunciado.requestFocus();
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
            this,
            mensagem,
            "Verifique os dados",
            JOptionPane.WARNING_MESSAGE
        );
    }
}
