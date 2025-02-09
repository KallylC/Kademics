package kademicsapp.kademicsapp.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kademicsapp.kademicsapp.Entity.Professor;

public class professorView {

    public void professorMenu() {
        int menuInput = 0;
        String[] menu = { "Cadastrar Professor", "Listar Professores", "Editar Informações", "Sair" };

        menuInput = JOptionPane.showOptionDialog(
                null,
                "Escolha uma opção:",
                "Kademics - Menu Professor",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                menu,
                menu[0]);

        switch (menuInput) {
            case 0:
                cadastrarProfessor();
                break;
            case 1:
                listarProfessores();
                break;
            case 2:
                editarProfessor();
                break;
            case 3:
                break;
            default:
                JOptionPane.showMessageDialog(null, "Nenhuma opção selecionada.");
                break;
        }
    }

    private void cadastrarProfessor() {
        boolean dadosValidos = false;

        while (!dadosValidos) {
            JTextField nomeInput = new JTextField();
            JTextField cpfInput = new JTextField();
            JTextField dataNascimentoInput = new JTextField();
            JTextField especialidadeInput = new JTextField();

            Object[] message = {
                    "Nome do Professor:", nomeInput,
                    "CPF:", cpfInput,
                    "Data de Nascimento (formato: DD/MM/AAAA):", dataNascimentoInput,
                    "Especialidade:", especialidadeInput
            };

            int result = JOptionPane.showConfirmDialog(null, message, "Cadastrar Professor",
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String nome = nomeInput.getText().trim();
                    String cpf = cpfInput.getText().trim();
                    String dataNascimento = dataNascimentoInput.getText().trim();
                    String especialidade = especialidadeInput.getText().trim();

                    if (nome.isEmpty() || cpf.isEmpty() || dataNascimento.isEmpty() || especialidade.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos!", "Aviso",
                                JOptionPane.WARNING_MESSAGE);
                        continue;
                    }

                    LocalDate nascimento = null;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                    try {
                        nascimento = LocalDate.parse(dataNascimento, formatter);
                        if (nascimento.isAfter(LocalDate.now())) {
                            JOptionPane.showMessageDialog(null, "A data de nascimento não pode ser futura.", "Erro",
                                    JOptionPane.ERROR_MESSAGE);
                            continue;
                        }
                    } catch (DateTimeParseException e) {
                        JOptionPane.showMessageDialog(null, "Data inválida. Use o formato: DD-MM-AAAA.", "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        continue;
                    }

                    Professor professor = new Professor();
                    professor.setNome(nome);
                    professor.setCpf(cpf);
                    professor.setDataNascimento(nascimento);
                    professor.setEspecialidade(especialidade);

                    enviarCadastroProfessor(professor);
                    break;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar o professor: " + e.getMessage(), "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;
            }
        }
    }

    private void enviarCadastroProfessor(Professor professor) {
        try {
            URL url = new URL("http://localhost:8080/api/professores");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String jsonInputString = objectMapper.writeValueAsString(professor);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonInputString.getBytes("utf-8"));
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(null, "Professor cadastrado com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao cadastrar professor. Código: " + responseCode, "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro na requisição: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarProfessores() {
        try {
            URL url = new URL("http://localhost:8080/api/professores");
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
                    List<Professor> professores = objectMapper.readValue(response.toString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Professor.class));

                    if (professores.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Nenhum professor cadastrado.", "Lista de Professores",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        StringBuilder lista = new StringBuilder("Lista de Professores:\n");
                        for (Professor p : professores) {
                            lista.append("ID: ").append(p.getIdProfessor())
                                    .append(", Nome: ").append(p.getNome())
                                    .append(", CPF: ").append(p.getCpf())
                                    .append(", especialidade: ").append(p.getEspecialidade())
                                    .append("\n");
                        }
                        JOptionPane.showMessageDialog(null, lista.toString(), "Lista de Professores",
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

    private void editarProfessor() {
        JOptionPane.showMessageDialog(null, "Funcionalidade de edição ainda não implementada.", "Em Breve",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
