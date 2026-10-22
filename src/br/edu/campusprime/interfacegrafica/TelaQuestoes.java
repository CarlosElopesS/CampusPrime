
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
    private static final Color AMARELO = new Color(250, 204, 21);
    private static final Color VERMELHO = new Color(248, 113, 113);
    private static final Color BRANCO = Color.WHITE;
    private static final Color CAMPO = new Color(240, 245, 248);

    private final ProvaRepositorio repositorio = new ProvaRepositorio();

    private final JComboBox<Prova> comboProvas = new JComboBox<>();

    private final JComboBox<String> comboTipo = new JComboBox<>(
        new String[]{"Múltipla escolha", "Discursiva"}
    );

    private final JTextArea campoEnunciado = new JTextArea(4, 30);
    private final JTextField campoValor = new JTextField();
    private final JTextField[] alternativas = new JTextField[4];

    private final JComboBox<String> comboCorreta = new JComboBox<>(
        new String[]{
            "Alternativa A", "Alternativa B",
            "Alternativa C", "Alternativa D"
        }
    );

    private final JTextField campoRespostaEsperada = new JTextField();

    private final DefaultListModel<String> modeloQuestoes =
        new DefaultListModel<>();

    private final JList<String> listaQuestoes =
        new JList<>(modeloQuestoes);

    private final JPanel painelAlternativas = new JPanel();
    private final JPanel painelDiscursiva = new JPanel();
    private final JPanel painelTipos = new JPanel(new CardLayout());

    private final JButton botaoSalvar = new JButton();
    private final JButton botaoCancelar = new JButton("Cancelar edição");

    private List<Prova> provas = new ArrayList<>();
    private int indiceEmEdicao = -1;

    public TelaQuestoes() {
        setTitle("CampusPrime | Cadastro de Questões");
        setSize(1000, 800);
        setMinimumSize(new Dimension(800, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        criarInterface();

        comboProvas.addActionListener(e -> {
            cancelarEdicao();
            atualizarListaQuestoes();
        });

        comboTipo.addActionListener(e -> atualizarTipoQuestao());

        carregarProvas();
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
            "Cadastre, edite e organize as questões das suas provas."
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
        formulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 75, 90)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        campoEnunciado.setBackground(CAMPO);
        campoEnunciado.setForeground(FUNDO);
        campoEnunciado.setCaretColor(FUNDO);
        campoEnunciado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoEnunciado.setLineWrap(true);
        campoEnunciado.setWrapStyleWord(true);

        configurarCampo(campoValor);
        configurarCampo(campoRespostaEsperada);

        comboProvas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboCorreta.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        adicionarCampo(formulario, "Prova:", comboProvas);
        adicionarCampo(formulario, "Tipo de questão:", comboTipo);
        adicionarCampo(
            formulario, "Enunciado:", new JScrollPane(campoEnunciado)
        );
        adicionarCampo(formulario, "Valor da questão:", campoValor);

        criarPainelAlternativas();
        criarPainelDiscursiva();

        painelTipos.add(painelAlternativas, "MULTIPLA");
        painelTipos.add(painelDiscursiva, "DISCURSIVA");

        painelTipos.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelTipos.setBackground(PAINEL);
        painelTipos.setBorder(new EmptyBorder(0, 0, 5, 0));

        formulario.add(painelTipos);

        botaoSalvar.setText("Cadastrar questão");
        estilizarBotao(botaoSalvar, VERDE);
        botaoSalvar.addActionListener(e -> salvarQuestao());

        estilizarBotao(botaoCancelar, AMARELO);
        botaoCancelar.setVisible(false);
        botaoCancelar.addActionListener(e -> cancelarEdicao());

        JButton botaoEditar = new JButton("Editar selecionada");
        estilizarBotao(botaoEditar, AZUL);
        botaoEditar.addActionListener(e -> iniciarEdicao());

        JButton botaoExcluir = new JButton("Excluir selecionada");
        estilizarBotao(botaoExcluir, VERMELHO);
        botaoExcluir.addActionListener(e -> excluirQuestao());

        JPanel botoesFormulario = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 5)
        );
        botoesFormulario.setOpaque(false);
        botoesFormulario.add(botaoCancelar);
        botoesFormulario.add(botaoSalvar);
        formulario.add(botoesFormulario);

        listaQuestoes.setBackground(PAINEL);
        listaQuestoes.setForeground(BRANCO);
        listaQuestoes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaQuestoes.setSelectionBackground(AZUL);
        listaQuestoes.setSelectionForeground(FUNDO);
        listaQuestoes.setFixedCellHeight(42);

        JLabel tituloLista = new JLabel("Questões da prova selecionada");
        tituloLista.setForeground(AZUL);
        tituloLista.setFont(new Font("Segoe UI", Font.BOLD, 17));

        JPanel painelLista = new JPanel(new BorderLayout(5, 8));
        painelLista.setOpaque(false);
        painelLista.add(tituloLista, BorderLayout.NORTH);

        JScrollPane rolagem = new JScrollPane(listaQuestoes);
        rolagem.setBorder(
            BorderFactory.createLineBorder(new Color(40, 75, 90))
        );
        rolagem.getViewport().setBackground(PAINEL);
        painelLista.add(rolagem, BorderLayout.CENTER);

        JPanel botoesLista = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 5)
        );
        botoesLista.setOpaque(false);
        botoesLista.add(botaoEditar);
        botoesLista.add(botaoExcluir);
        painelLista.add(botoesLista, BorderLayout.SOUTH);

        JSplitPane divisao = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT, formulario, painelLista
        );
        divisao.setResizeWeight(0.65);
        divisao.setDividerLocation(475);
        divisao.setBorder(null);
        divisao.setOpaque(false);
        divisao.setContinuousLayout(true);

        JLabel rodape = new JLabel(
            "CampusPrime • Gestão de Avaliações Acadêmicas"
        );
        rodape.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rodape.setForeground(new Color(180, 200, 210));

        principal.add(cabecalho, BorderLayout.NORTH);
        principal.add(divisao, BorderLayout.CENTER);
        principal.add(rodape, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private void criarPainelAlternativas() {
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
            painelAlternativas, "Resposta correta:", comboCorreta
        );
    }

    private void criarPainelDiscursiva() {
        painelDiscursiva.setLayout(
            new BoxLayout(painelDiscursiva, BoxLayout.Y_AXIS)
        );
        painelDiscursiva.setBackground(PAINEL);

        adicionarCampo(
            painelDiscursiva,
            "Resposta esperada (para auxiliar na correção):",
            campoRespostaEsperada
        );
    }

    private void adicionarCampo(
            JPanel painel, String texto, JComponent componente) {

        JLabel label = new JLabel(texto);
        label.setForeground(BRANCO);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (componente instanceof JScrollPane) {
            componente.setPreferredSize(new Dimension(400, 85));
            componente.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 95)
            );
        } else {
            componente.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 35)
            );
        }

        painel.add(label);
        painel.add(Box.createVerticalStrut(4));
        painel.add(componente);
        painel.add(Box.createVerticalStrut(7));
    }

    private void configurarCampo(JTextField campo) {
        campo.setBackground(CAMPO);
        campo.setForeground(FUNDO);
        campo.setCaretColor(FUNDO);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 195, 205)),
            new EmptyBorder(6, 8, 6, 8)
        ));
    }

    private void estilizarBotao(JButton botao, Color cor) {
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setBackground(cor);
        botao.setForeground(FUNDO);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(9, 12, 9, 12));
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
                            prova.getTitulo() + " — "
                                + prova.getDisciplina()
                        );
                    }

                    return this;
                }
            });

            atualizarListaQuestoes();

            if (provas.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Cadastre uma prova na Tela 01 antes de adicionar questões.",
                    "Nenhuma prova cadastrada",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (IOException | RuntimeException erro) {
            mostrarErro(
                "Não foi possível carregar as provas:\n"
                    + erro.getMessage()
            );
        }
    }

    private void atualizarTipoQuestao() {
        CardLayout layout = (CardLayout) painelTipos.getLayout();

        if (comboTipo.getSelectedIndex() == 0) {
            layout.show(painelTipos, "MULTIPLA");
        } else {
            layout.show(painelTipos, "DISCURSIVA");
        }

        painelTipos.revalidate();
        painelTipos.repaint();
    }

    private Questao criarQuestaoDoFormulario() {
        String enunciado = campoEnunciado.getText().trim();

        if (enunciado.isEmpty()) {
            throw new IllegalArgumentException(
                "Informe o enunciado da questão."
            );
        }

        double valor;

        try {
            valor = Double.parseDouble(
                campoValor.getText().trim().replace(",", ".")
            );
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException(
                "Informe um valor numérico válido."
            );
        }

        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(
                "O valor deve ser maior que zero."
            );
        }

        if (comboTipo.getSelectedIndex() == 0) {
            List<String> opcoes = new ArrayList<>();

            System.out.println("--- Verificação das alternativas ---");

            for (int i = 0; i < alternativas.length; i++) {
                System.out.println(
                    "Alternativa " + (i + 1) + ": ["
                        + alternativas[i].getText() + "]"
                );
            }

            for (JTextField alternativa : alternativas) {
                String texto = alternativa.getText().trim();

                if (texto.isEmpty()) {
                    throw new IllegalArgumentException(
                        "Preencha as quatro alternativas."
                    );
                }

                opcoes.add(texto);
            }

            return new QuestaoMultiplaEscolha(
                enunciado,
                valor,
                opcoes,
                comboCorreta.getSelectedIndex()
            );
        }

        String esperada = campoRespostaEsperada.getText().trim();

        if (esperada.isEmpty()) {
            throw new IllegalArgumentException(
                "Informe a resposta esperada."
            );
        }

        return new QuestaoDiscursiva(enunciado, valor, esperada);
    }

    private void salvarQuestao() {
        Prova prova = (Prova) comboProvas.getSelectedItem();

        if (prova == null) {
            mostrarAviso("Cadastre e selecione uma prova primeiro.");
            return;
        }

        boolean estavaEditando = indiceEmEdicao >= 0;

        try {
            Questao novaQuestao = criarQuestaoDoFormulario();

            if (estavaEditando) {
                prova.substituirQuestao(indiceEmEdicao, novaQuestao);
            } else {
                prova.adicionarQuestao(novaQuestao);
            }

            repositorio.atualizarTodas(provas);

            atualizarListaQuestoes();
            cancelarEdicao();

            JOptionPane.showMessageDialog(
                this,
                estavaEditando
                    ? "Questão atualizada com sucesso!"
                    : "Questão cadastrada com sucesso!",
                "CampusPrime",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException | IllegalArgumentException
                 | IndexOutOfBoundsException erro) {
            mostrarErro(
                "Não foi possível salvar a questão:\n"
                    + erro.getMessage()
            );
        }
    }

    private void iniciarEdicao() {
        int indice = listaQuestoes.getSelectedIndex();
        Prova prova = (Prova) comboProvas.getSelectedItem();

        if (prova == null || indice < 0) {
            mostrarAviso("Selecione uma questão para editar.");
            return;
        }

        Questao questao = prova.getQuestoes().get(indice);

        indiceEmEdicao = indice;
        campoEnunciado.setText(questao.getEnunciado());
        campoValor.setText(String.valueOf(questao.getValor()));

        if (questao instanceof QuestaoMultiplaEscolha multipla) {
            comboTipo.setSelectedIndex(0);

            List<String> opcoes = multipla.getAlternativas();

            for (int i = 0; i < alternativas.length; i++) {
                alternativas[i].setText(
                    i < opcoes.size() ? opcoes.get(i) : ""
                );
            }

            comboCorreta.setSelectedIndex(
                multipla.getIndiceCorreto()
            );
            campoRespostaEsperada.setText("");

        } else if (questao instanceof QuestaoDiscursiva discursiva) {
            comboTipo.setSelectedIndex(1);

            for (JTextField alternativa : alternativas) {
                alternativa.setText("");
            }

            campoRespostaEsperada.setText(
                discursiva.getRespostaEsperada()
            );
        }

        atualizarTipoQuestao();
        botaoSalvar.setText("Salvar alterações");
        botaoCancelar.setVisible(true);
        campoEnunciado.requestFocus();
    }

    private void excluirQuestao() {
        int indice = listaQuestoes.getSelectedIndex();
        Prova prova = (Prova) comboProvas.getSelectedItem();

        if (prova == null || indice < 0) {
            mostrarAviso("Selecione uma questão para excluir.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente excluir a questão selecionada?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            prova.removerQuestao(indice);
            repositorio.atualizarTodas(provas);

            cancelarEdicao();
            atualizarListaQuestoes();

            JOptionPane.showMessageDialog(
                this,
                "Questão excluída com sucesso.",
                "CampusPrime",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException | IndexOutOfBoundsException erro) {
            mostrarErro(
                "Não foi possível excluir a questão:\n"
                    + erro.getMessage()
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
                ? "Múltipla escolha"
                : "Discursiva";

            modeloQuestoes.addElement(
                numero + ". [" + tipo + "] "
                    + questao.getEnunciado()
                    + " — "
                    + String.format("%.2f", questao.getValor())
                    + " ponto(s)"
            );

            numero++;
        }
    }

    private void cancelarEdicao() {
        indiceEmEdicao = -1;
        campoEnunciado.setText("");
        campoValor.setText("");

        for (JTextField alternativa : alternativas) {
            alternativa.setText("");
        }

        campoRespostaEsperada.setText("");
        comboCorreta.setSelectedIndex(0);
        comboTipo.setSelectedIndex(0);

        botaoSalvar.setText("Cadastrar questão");
        botaoCancelar.setVisible(false);

        atualizarTipoQuestao();
    }

    private void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(
            this, mensagem, "Atenção",
            JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
            this, mensagem, "Erro",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
