
package br.edu.campusprime.interfacegrafica;

import br.edu.campusprime.modelo.Prova;
import br.edu.campusprime.repositorio.ProvaRepositorio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class TelaProvas extends JFrame {

    private static final Color FUNDO = new Color(11, 25, 36);
    private static final Color PAINEL = new Color(21, 51, 66);
    private static final Color AZUL = new Color(56, 189, 248);
    private static final Color VERDE = new Color(45, 212, 191);
    private static final Color BRANCO = Color.WHITE;
    private static final Color TEXTO = new Color(200, 215, 225);

    private final ProvaRepositorio repositorio;
    private final DefaultListModel<Prova> modeloLista;
    private final JList<Prova> listaProvas;

    private JTextField campoTitulo;
    private JTextField campoDisciplina;

    public TelaProvas() {
        repositorio = new ProvaRepositorio();
        modeloLista = new DefaultListModel<>();
        listaProvas = new JList<>(modeloLista);

        configurarJanela();
        criarInterface();
        carregarProvas();
    }

    private void configurarJanela() {
        setTitle("CampusPrime | Gerenciar Provas");
        setSize(850, 600);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void criarInterface() {
        JPanel principal = new JPanel(new BorderLayout(20, 20));
        principal.setBackground(FUNDO);
        principal.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Cabeçalho
        JLabel titulo = new JLabel("Gerenciar provas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(AZUL);

        JLabel subtitulo = new JLabel(
            "Cadastre e consulte suas avaliações acadêmicas."
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(BRANCO);

        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);
        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(8));
        cabecalho.add(subtitulo);

        // Formulário
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(PAINEL);
        formulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 75, 90)),
            new EmptyBorder(18, 18, 18, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoTitulo = criarCampo();
        campoDisciplina = criarCampo();

        adicionarCampo(
            formulario, gbc, 0, "Título da prova:", campoTitulo
        );

        adicionarCampo(
            formulario, gbc, 1, "Disciplina:", campoDisciplina
        );

        JButton botaoSalvar = new JButton("Salvar prova");
        estilizarBotao(botaoSalvar, AZUL);
        botaoSalvar.addActionListener(e -> salvarProva());

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        formulario.add(botaoSalvar, gbc);

        // Lista de provas
        JLabel tituloLista = new JLabel("Provas cadastradas");
        tituloLista.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tituloLista.setForeground(VERDE);
        tituloLista.setBorder(new EmptyBorder(0, 0, 10, 0));

        listaProvas.setBackground(PAINEL);
        listaProvas.setForeground(BRANCO);
        listaProvas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listaProvas.setSelectionBackground(AZUL);
        listaProvas.setSelectionForeground(FUNDO);
        listaProvas.setFixedCellHeight(38);

        // Exibe o título e a disciplina, não o identificador do objeto.
        listaProvas.setCellRenderer((lista, prova, indice, selecionado, foco) -> {
            JLabel item = new JLabel(
                prova.getTitulo() + "  —  " + prova.getDisciplina()
            );

            item.setOpaque(true);
            item.setBorder(new EmptyBorder(8, 10, 8, 10));
            item.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            if (selecionado) {
                item.setBackground(AZUL);
                item.setForeground(FUNDO);
            } else {
                item.setBackground(PAINEL);
                item.setForeground(BRANCO);
            }

            return item;
        });

        JScrollPane rolagem = new JScrollPane(listaProvas);
        rolagem.setBorder(
            BorderFactory.createLineBorder(new Color(40, 75, 90))
        );
        rolagem.getViewport().setBackground(PAINEL);

        JPanel areaLista = new JPanel(new BorderLayout());
        areaLista.setOpaque(false);
        areaLista.add(tituloLista, BorderLayout.NORTH);
        areaLista.add(rolagem, BorderLayout.CENTER);

        // Organização das áreas
        JPanel conteudo = new JPanel(new GridLayout(2, 1, 0, 15));
        conteudo.setOpaque(false);
        conteudo.add(formulario);
        conteudo.add(areaLista);

        JLabel rodape = new JLabel(
            "CampusPrime • Gestão de Avaliações Acadêmicas"
        );
        rodape.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rodape.setForeground(new Color(180, 200, 210));

        principal.add(cabecalho, BorderLayout.NORTH);
        principal.add(conteudo, BorderLayout.CENTER);
        principal.add(rodape, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private JTextField criarCampo() {
        JTextField campo = new JTextField();

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBackground(FUNDO);
        campo.setForeground(BRANCO);
        campo.setCaretColor(BRANCO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 90, 105)),
            new EmptyBorder(8, 8, 8, 8)
        ));

        return campo;
    }

    private void adicionarCampo(
            JPanel painel,
            GridBagConstraints gbc,
            int linha,
            String rotulo,
            JTextField campo) {

        JLabel label = new JLabel(rotulo);
        label.setForeground(BRANCO);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.weightx = 0;
        painel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(campo, gbc);
    }

    private void estilizarBotao(JButton botao, Color destaque) {
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setBackground(destaque);
        botao.setForeground(FUNDO);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(10, 18, 10, 18));
    }

    private void salvarProva() {
        String titulo = campoTitulo.getText().trim();
        String disciplina = campoDisciplina.getText().trim();

        if (titulo.isEmpty() || disciplina.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Preencha o título e a disciplina.",
                "Campos obrigatórios",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            Prova prova = new Prova(titulo, disciplina);
            repositorio.salvar(prova);

            modeloLista.addElement(prova);

            campoTitulo.setText("");
            campoDisciplina.setText("");

            JOptionPane.showMessageDialog(
                this,
                "Prova salva com sucesso!",
                "CampusPrime",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException | IllegalArgumentException erro) {
            JOptionPane.showMessageDialog(
                this,
                "Não foi possível salvar a prova:\n" + erro.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void carregarProvas() {
        try {
            List<Prova> provas = repositorio.carregar();

            modeloLista.clear();

            for (Prova prova : provas) {
                modeloLista.addElement(prova);
            }

        } catch (IOException | RuntimeException erro) {
            JOptionPane.showMessageDialog(
                this,
                "Não foi possível carregar as provas:\n" + erro.getMessage(),
                "Erro de leitura",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
