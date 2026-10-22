
package br.edu.campusprime.interfacegrafica;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    private static final Color FUNDO = new Color(11, 25, 36);
    private static final Color PAINEL = new Color(21, 51, 66);
    private static final Color AZUL = new Color(56, 189, 248);
    private static final Color VERDE = new Color(45, 212, 191);
    private static final Color AMARELO = new Color(250, 204, 21);
    private static final Color BRANCO = Color.WHITE;

    public TelaPrincipal() {
        setTitle("CampusPrime | Gestão de Avaliações");
        setSize(1000, 650);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        criarInterface();
    }

    private void criarInterface() {
        JPanel principal = new JPanel(new BorderLayout(20, 25));
        principal.setBackground(FUNDO);
        principal.setBorder(new EmptyBorder(30, 35, 25, 35));

        JLabel titulo = new JLabel("CampusPrime");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titulo.setForeground(AZUL);

        JLabel subtitulo = new JLabel(
            "Gestão Inteligente de Avaliações Acadêmicas"
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitulo.setForeground(BRANCO);

        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(
            new BoxLayout(cabecalho, BoxLayout.Y_AXIS)
        );
        cabecalho.setOpaque(false);
        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(8));
        cabecalho.add(subtitulo);

        JPanel conteudo = new JPanel(
            new GridLayout(2, 2, 18, 18)
        );
        conteudo.setOpaque(false);

        JButton botaoProvas = criarBotao(
            "01 | PROVAS",
            "Cadastrar e consultar avaliações",
            AZUL
        );

        JButton botaoQuestoes = criarBotao(
            "02 | QUESTÕES",
            "Questões objetivas e discursivas",
            VERDE
        );

        JButton botaoCorrecao = criarBotao(
            "03 | CORREÇÃO",
            "Registrar respostas e calcular notas",
            AMARELO
        );

        JButton botaoRelatorios = criarBotao(
            "04 | RELATÓRIOS",
            "Consultar resumos das avaliações",
            AZUL
        );

        botaoProvas.addActionListener(e ->
            abrirTela("Provas", () ->
                new TelaProvas().setVisible(true)
            )
        );

        botaoQuestoes.addActionListener(e ->
            abrirTela("Questões", () ->
                new TelaQuestoes().setVisible(true)
            )
        );

        botaoCorrecao.addActionListener(e ->
            abrirTela("Correção", () ->
                new TelaCorrecao().setVisible(true)
            )
        );

        botaoRelatorios.addActionListener(e ->
            abrirTela("Relatórios", () ->
                new TelaRelatorios().setVisible(true)
            )
        );

        conteudo.add(botaoProvas);
        conteudo.add(botaoQuestoes);
        conteudo.add(botaoCorrecao);
        conteudo.add(botaoRelatorios);

        JLabel rodape = new JLabel(
            "CampusPrime • Sistema acadêmico em desenvolvimento"
        );
        rodape.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rodape.setForeground(new Color(180, 200, 210));

        principal.add(cabecalho, BorderLayout.NORTH);
        principal.add(conteudo, BorderLayout.CENTER);
        principal.add(rodape, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private JButton criarBotao(
            String titulo, String descricao, Color destaque) {

        JButton botao = new JButton(
            "<html><div style='text-align:left;'>"
                + "<span style='color:#38BDF8;font-size:12px;'>"
                + titulo
                + "</span><br><br>"
                + "<span style='color:#FFFFFF;font-size:18px;'>"
                + descricao
                + "</span></div></html>"
        );

        botao.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        botao.setBackground(PAINEL);
        botao.setForeground(BRANCO);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        botao.setBorderPainted(true);
        botao.setFocusPainted(false);
        botao.setRolloverEnabled(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setVerticalAlignment(SwingConstants.CENTER);

        botao.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(destaque, 2),
                new EmptyBorder(25, 25, 25, 25)
            )
        );

        return botao;
    }

    private void abrirTela(String nome, Runnable acao) {
        try {
            acao.run();
        } catch (Exception ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                this,
                "Não foi possível abrir a tela de " + nome
                    + ".\nDetalhes: " + ex.getMessage(),
                "CampusPrime | Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
