
package br.edu.campusprime.repositorio;

import br.edu.campusprime.modelo.Prova;
import br.edu.campusprime.modelo.Questao;
import br.edu.campusprime.modelo.QuestaoDiscursiva;
import br.edu.campusprime.modelo.QuestaoMultiplaEscolha;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProvaRepositorio {

    private final Path arquivo;

    public ProvaRepositorio() {
        this(Paths.get("dados", "provas.json"));
    }

    public ProvaRepositorio(Path arquivo) {
        if (arquivo == null) {
            throw new IllegalArgumentException(
                "O caminho do arquivo não pode ser nulo."
            );
        }

        this.arquivo = arquivo;
    }

    public void salvar(Prova prova) throws IOException {
        if (prova == null) {
            throw new IllegalArgumentException(
                "A prova não pode ser nula."
            );
        }

        List<Prova> provas = carregar();
        provas.add(prova);
        salvarTodas(provas);
    }

    public List<Prova> carregar() throws IOException {
        List<Prova> provas = new ArrayList<>();

        if (!Files.exists(arquivo)) {
            return provas;
        }

        String json = Files.readString(
            arquivo,
            StandardCharsets.UTF_8
        );

        Pattern padraoProvas = Pattern.compile(
            "\\{\\s*\"titulo\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"\\s*,"
            + "\\s*\"disciplina\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"\\s*,"
            + "\\s*\"questoes\"\\s*:\\s*\\[(.*?)\\]\\s*\\}",
            Pattern.DOTALL
        );

        Matcher provasEncontradas = padraoProvas.matcher(json);

        while (provasEncontradas.find()) {
            Prova prova = new Prova(
                desescaparJson(provasEncontradas.group(1)),
                desescaparJson(provasEncontradas.group(2))
            );

            String blocoQuestoes = provasEncontradas.group(3);

            Pattern padraoQuestoes = Pattern.compile(
                "\\{\\s*\"tipo\"\\s*:\\s*\"(multipla|discursiva)\""
                + "\\s*,\\s*\"enunciado\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\""
                + "\\s*,\\s*\"valor\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)"
                + "\\s*,\\s*\"dados\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"\\s*\\}",
                Pattern.DOTALL
            );

            Matcher questoesEncontradas =
                padraoQuestoes.matcher(blocoQuestoes);

            while (questoesEncontradas.find()) {
                String tipo = questoesEncontradas.group(1);

                String enunciado = desescaparJson(
                    questoesEncontradas.group(2)
                );

                double valor = Double.parseDouble(
                    questoesEncontradas.group(3)
                );

                String dados = desescaparJson(
                    questoesEncontradas.group(4)
                );

                if (tipo.equals("discursiva")) {
                    QuestaoDiscursiva questao =
                        new QuestaoDiscursiva(
                            enunciado,
                            valor,
                            dados
                        );

                    prova.adicionarQuestao(questao);

                } else {
                    String[] partes = dados.split("\\|", -1);

                    if (partes.length < 3) {
                        throw new IOException(
                            "Dados de questão objetiva inválidos."
                        );
                    }

                    int indiceCorreto;

                    try {
                        indiceCorreto = Integer.parseInt(partes[0]);
                    } catch (NumberFormatException ex) {
                        throw new IOException(
                            "Índice da alternativa correta inválido.",
                            ex
                        );
                    }

                    List<String> alternativas = new ArrayList<>();

                    for (int i = 1; i < partes.length; i++) {
                        alternativas.add(partes[i]);
                    }

                    QuestaoMultiplaEscolha questao =
                        new QuestaoMultiplaEscolha(
                            enunciado,
                            valor,
                            alternativas,
                            indiceCorreto
                        );

                    prova.adicionarQuestao(questao);
                }
            }

            provas.add(prova);
        }

        return provas;
    }

    public void atualizarTodas(List<Prova> provas)
            throws IOException {

        if (provas == null) {
            throw new IllegalArgumentException(
                "A lista de provas não pode ser nula."
            );
        }

        salvarTodas(provas);
    }

    private void salvarTodas(List<Prova> provas)
            throws IOException {

        Path pasta = arquivo.getParent();

        if (pasta != null) {
            Files.createDirectories(pasta);
        }

        StringBuilder json = new StringBuilder("[\n");

        for (int i = 0; i < provas.size(); i++) {
            Prova prova = provas.get(i);

            json.append("  {\n");

            json.append("    \"titulo\": \"")
                .append(escaparJson(prova.getTitulo()))
                .append("\",\n");

            json.append("    \"disciplina\": \"")
                .append(escaparJson(prova.getDisciplina()))
                .append("\",\n");

            json.append("    \"questoes\": [\n");

            List<Questao> questoes = prova.getQuestoes();

            for (int j = 0; j < questoes.size(); j++) {
                Questao questao = questoes.get(j);

                String tipo;
                String dados;

                if (questao instanceof QuestaoDiscursiva discursiva) {
                    tipo = "discursiva";
                    dados = discursiva.getRespostaEsperada();

                } else if (
                    questao instanceof QuestaoMultiplaEscolha multipla
                ) {
                    tipo = "multipla";

                    StringBuilder alternativas = new StringBuilder();

                    alternativas.append(multipla.getIndiceCorreto());

                    for (String alternativa :
                            multipla.getAlternativas()) {

                        alternativas.append("|").append(alternativa);
                    }

                    dados = alternativas.toString();

                } else {
                    throw new IOException(
                        "Tipo de questão não suportado para salvar."
                    );
                }

                json.append("      {\n");

                json.append("        \"tipo\": \"")
                    .append(tipo)
                    .append("\",\n");

                json.append("        \"enunciado\": \"")
                    .append(escaparJson(questao.getEnunciado()))
                    .append("\",\n");

                json.append("        \"valor\": ")
                    .append(questao.getValor())
                    .append(",\n");

                json.append("        \"dados\": \"")
                    .append(escaparJson(dados))
                    .append("\"\n");

                json.append("      }");

                if (j < questoes.size() - 1) {
                    json.append(",");
                }

                json.append("\n");
            }

            json.append("    ]\n");
            json.append("  }");

            if (i < provas.size() - 1) {
                json.append(",");
            }

            json.append("\n");
        }

        json.append("]\n");

        Files.writeString(
            arquivo,
            json.toString(),
            StandardCharsets.UTF_8
        );
    }

    private String escaparJson(String texto) {
        return texto
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    private String desescaparJson(String texto) {
        StringBuilder resultado = new StringBuilder();
        boolean escape = false;

        for (int i = 0; i < texto.length(); i++) {
            char caractere = texto.charAt(i);

            if (escape) {
                switch (caractere) {
                    case 'n' -> resultado.append('\n');
                    case 'r' -> resultado.append('\r');
                    case 't' -> resultado.append('\t');
                    case '"' -> resultado.append('"');
                    case '\\' -> resultado.append('\\');
                    default -> resultado.append(caractere);
                }

                escape = false;

            } else if (caractere == '\\') {
                escape = true;

            } else {
                resultado.append(caractere);
            }
        }

        if (escape) {
            resultado.append('\\');
        }

        return resultado.toString();
    }
}
