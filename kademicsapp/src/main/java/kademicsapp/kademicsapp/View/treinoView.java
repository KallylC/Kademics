package kademicsapp.kademicsapp.View;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kademicsapp.kademicsapp.Entity.Treino;


public class treinoView {

    public void treinoMenu() {
        int menuInput = 0;
        String[] menu = { "Fazer treino", "Listar treinos", "atribuir treino", "Sair" };

        menuInput = JOptionPane.showOptionDialog(
                null,
                "Escolha uma opção:",
                "Kademics - Menu treino",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                menu,
                menu[0]);

        switch (menuInput) {
            case 0:
                fazerTreino();
                break;
            case 1:
                listarTreinos();
                break;
            case 2:
                atribuirTreino();
                break;
            case 3:
                break;
            default:
                JOptionPane.showMessageDialog(null, "Nenhuma opção selecionada.");
                break;
        }
    }

    private void fazerTreino() {
        boolean dadosValidos = false;

        while (!dadosValidos) {
        JTextField tipoExercicioInput = new JTextField();
        JTextField duracaoInput = new JTextField();

        Object[] message = {
            "Tipo de Exercício:", tipoExercicioInput,
            "Duração do Treino (em minutos):", duracaoInput
        };

        int result = JOptionPane.showConfirmDialog(null, message, "Fazer Treino", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String tipoExercicio = tipoExercicioInput.getText().trim();
                String duracaoStr = duracaoInput.getText().trim();

                if (tipoExercicio.isEmpty() || duracaoStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos!", "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                Treino treino = new Treino();
                treino.setTipoExercicio(tipoExercicio);
                treino.setDuracao(duracaoStr);

                enviarTreino(treino);
                dadosValidos = true; // Finaliza o loop
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "A duração deve ser um número válido.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao criar o treino: " + e.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            break; 
        }
    }
}

    private void enviarTreino(Treino treino) {
        try {
            URL url = new URL("http://localhost:8080/api/treinos");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInputString = objectMapper.writeValueAsString(treino);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonInputString.getBytes("utf-8"));
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(null, "Treino cadastrado com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao cadastrar treino. Código: " + responseCode, "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro na requisição: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atribuirTreino() {
        boolean dadosValidos = false;
    
        while (!dadosValidos) {
            JTextField idTreinoInput = new JTextField();
            JTextField nomeProfessorInput = new JTextField();
            JTextField nomeAlunoInput = new JTextField();
    
            Object[] message = {
                    "ID do Treino:", idTreinoInput,
                    "Nome do Professor:", nomeProfessorInput,
                    "Nome do Aluno:", nomeAlunoInput
            };
    
            int result = JOptionPane.showConfirmDialog(null, message, "Atribuir Treino", JOptionPane.OK_CANCEL_OPTION);
    
            if (result == JOptionPane.OK_OPTION) {
                String idTreino = idTreinoInput.getText().trim();
                String nomeProfessor = nomeProfessorInput.getText().trim();
                String nomeAluno = nomeAlunoInput.getText().trim();
    
                try {
                    // Validação de campos vazios
                    validarCamposPreenchidos(idTreino, nomeProfessor, nomeAluno);
    
                    // Lista com os nomes a serem buscados
                    List<String> nomes = List.of(nomeProfessor, nomeAluno);
    
                    // Buscar IDs pelo nome em uma única requisição
                    Map<String, Long> ids = buscarIdsPorNomes(nomes);
    
                    // Validar se os IDs foram encontrados
                    if (!ids.containsKey(nomeProfessor)) {
                        throw new IllegalArgumentException("Professor não encontrado: " + nomeProfessor);
                    }
                    if (!ids.containsKey(nomeAluno)) {
                        throw new IllegalArgumentException("Aluno não encontrado: " + nomeAluno);
                    }
    
                    long idProfessor = ids.get(nomeProfessor);
                    long idAluno = ids.get(nomeAluno);
    
                    // Atualizar treino com IDs
                    enviarAtualizacaoTreino(idTreino, idProfessor, idAluno);
                    dadosValidos = true; // Finaliza o loop após sucesso
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;
            }
        }
    }
    
    private void validarCamposPreenchidos(String... campos) {
        for (String campo : campos) {
            if (campo.isBlank()) {
                throw new IllegalArgumentException("Todos os campos devem ser preenchidos!");
            }
        }
    }
    
    private Map<String, Long> buscarIdsPorNomes(List<String> nomes) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://localhost:8080/api/treinos/util/buscarIds");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
    
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInput = objectMapper.writeValueAsString(nomes);
    
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }
    
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream()) {
                    return objectMapper.readValue(inputStream, new TypeReference<Map<String, Long>>() {});
                }
            } else {
                throw new Exception("Erro na requisição: " + responseCode);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    
    private void enviarAtualizacaoTreino(String idTreino, Long idProfessor, Long idAluno) {
        try {
            URL url = new URL("http://localhost:8080/api/treinos/" + idTreino);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
    
            // Cria o JSON de entrada
            String jsonInputString = String.format(
            "{\"professor\": {\"idProfessor\": %d}, \"aluno\": {\"idAluno\": %d}}", 
            idProfessor, idAluno
            );
            System.out.println(jsonInputString);
    
            // Envia os dados
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonInputString.getBytes(StandardCharsets.UTF_8));
            }
            
            // Captura o código de resposta
            int responseCode = connection.getResponseCode();
    
            // Captura a mensagem de resposta do servidor
            try (InputStream is = (responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream())) {
                String responseMessage = new BufferedReader(new InputStreamReader(is))
                        .lines()
                        .reduce("", (acc, line) -> acc + line);
    
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JOptionPane.showMessageDialog(null, "Treino atualizado com sucesso!\n" + responseMessage, "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao atualizar treino.\nCódigo: " + responseCode +
                            "\nResposta: " + responseMessage, "Erro", JOptionPane.ERROR_MESSAGE);
                }
                System.out.println("Enviando requisição PUT para: " + url);
                System.out.println("JSON enviado: " + jsonInputString);
                System.out.println("Código de resposta: " + responseCode);

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro na atualização: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarTreinos() {
        try {
            URL url = new URL("http://localhost:8080/api/treinos");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
    
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                     BufferedReader bufferedReader = new BufferedReader(reader)) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
    
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    List<Treino> treinos = objectMapper.readValue(response.toString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Treino.class));
    
                    if (treinos.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Nenhum treino cadastrado.", "Lista de Treinos",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        StringBuilder lista = new StringBuilder("Lista de Treinos:\n");
                        for (Treino t : treinos) {
                            lista.append("ID: ").append(t.getIdTreino())
                                    .append(", Tipo: ").append(t.getTipoExercicio())
                                    .append(", Duração: ").append(t.getDuracao())
                                    .append("\nProfessor: ").append(getNomeProfessor(t))
                                    .append("\nAluno: ").append(getNomeAluno(t))
                                    .append("\n------------------------\n");
                        }
                        JOptionPane.showMessageDialog(null, lista.toString(), "Lista de Treinos",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao buscar lista. Código: " + responseCode, "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro na requisição: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Métodos auxiliares para tratamento de nulidade
    private String getNomeProfessor(Treino treino) {
        return (treino.getProfessor() != null && treino.getProfessor().getNome() != null) 
                ? treino.getProfessor().getNome() 
                : "Não atribuído ainda!";
    }
    
    private String getNomeAluno(Treino treino) {
        return (treino.getAluno() != null && treino.getAluno().getNome() != null) 
                ? treino.getAluno().getNome() 
                : "Não atribuído ainda!";
    }
}