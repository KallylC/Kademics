package kademicsapp.kademicsapp.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URL;
import kademicsapp.kademicsapp.Controller.alunoController;
import kademicsapp.kademicsapp.Entity.Aluno;

public class alunoView {
    private alunoController alunoController;

    // Construtor que cria uma nova instância de alunoController diretamente
    public alunoView() {
        this.alunoController = new alunoController();  // Criando o controller diretamente na view
    }

    // Exibe o menu de opções
    public void alunoViews() {
        int menuInput = 0;
        String[] menu = { "Cadastrar Aluno", "Lista Alunos", "Editar Informações", "Sair" };

        // Exibe o menu de opções
        menuInput = JOptionPane.showOptionDialog(
            null,
            "Escolha uma opção:",
            "Kademics - Menu",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            menu,
            menu[0]
        );

        // Processa a escolha do usuário
        switch (menuInput) {
            case 0:
                CadastrarAluno();
                break;
            case 1:
                ListaAlunos();
                break;
            case 2:
                EditarAlunos();
                break;
            case 3:
                break;
            default:
                JOptionPane.showMessageDialog(null, "Nenhuma opção selecionada.");
                break;
        }
    }

    // Função para cadastrar aluno
    private void CadastrarAluno() {
        boolean dadosValidos = false;

        while (!dadosValidos) {
            JTextField nomeInput = new JTextField();
            JTextField cpfInput = new JTextField();
            JTextField dataNascimentoInput = new JTextField();
            JTextField telefoneInput = new JTextField();

            Object[] message = {
                "Nome do Aluno:", nomeInput,
                "CPF:", cpfInput,
                "Data de Nascimento (formato: DD/MM/AAAA):", dataNascimentoInput,
                "Telefone:", telefoneInput
            };

            int result = JOptionPane.showConfirmDialog(null, message, "Cadastrar Aluno", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String nome = nomeInput.getText().trim();
                    String cpf = cpfInput.getText().trim();
                    String dataNascimento = dataNascimentoInput.getText().trim();
                    String telefone = telefoneInput.getText().trim();

                    if (nome.isEmpty() || cpf.isEmpty() || dataNascimento.isEmpty() || telefone.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos!", "Aviso", JOptionPane.WARNING_MESSAGE);
                        continue;
                    }

                    LocalDate nascimento = null;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // Formato desejado

                    try {
                        nascimento = LocalDate.parse(dataNascimento, formatter);
                        // Verifica se a data de nascimento é no futuro
                        if (nascimento.isAfter(LocalDate.now())) {
                            JOptionPane.showMessageDialog(null, "A data de nascimento não pode ser futura.", "Erro", JOptionPane.ERROR_MESSAGE);
                            continue;
                        }
                    } catch (DateTimeParseException e) {
                        JOptionPane.showMessageDialog(null, "Data de nascimento inválida. Use o formato: DD-MM-AAAA.", "Erro", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }

                    // Cálculo da idade
                    int idade = LocalDate.now().getYear() - nascimento.getYear();
                    if (LocalDate.now().getDayOfYear() < nascimento.getDayOfYear()) {
                        idade--;
                    }

                    // Validação de idade mínima e máxima
                    if (idade < 18) {
                        JOptionPane.showMessageDialog(null, "Idade mínima para cadastro é 18 anos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                        continue;
                    }

                    if (idade > 120) {
                        JOptionPane.showMessageDialog(null, "Idade inválida. Verifique a data de nascimento.", "Erro", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }

                    Aluno aluno = new Aluno();
                    aluno.setNome(nome);
                    aluno.setCpf(cpf);
                    aluno.setDataNascimento(nascimento);
                    aluno.setIdade(idade);
                    aluno.setTelefone(telefone);

                    // Enviar os dados via requisição HTTP POST
                    try {
                    URL url = new URL("http://localhost:8080/api/alunos");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setConnectTimeout(5000); 
                    connection.setReadTimeout(5000);   

                    // Converte o objeto aluno para JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    String jsonInputString = objectMapper.writeValueAsString(aluno); // Converte o objeto aluno para JSON

                    // Enviar a requisição
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Verifica o código de resposta
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                            BufferedReader bufferedReader = new BufferedReader(reader)) {
                            String responseLine;
                            StringBuilder response = new StringBuilder();
                            while ((responseLine = bufferedReader.readLine()) != null) {
                                response.append(responseLine);
                            }
                            System.out.println("Resposta do servidor: " + response.toString());
                        }

                        JOptionPane.showMessageDialog(null, "Aluno cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                        connection.disconnect();
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Erro ao cadastrar o aluno! Código de resposta: " + responseCode, "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Erro ao realizar requisição HTTP: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } 


                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar o aluno: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;
            }
        }
    }

    

    // Função para listar alunos cadastrados
    private void ListaAlunos() {
        try {
            URL url = new URL("http://localhost:8080/api/alunos"); 
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000); 
            connection.setReadTimeout(5000);    
    
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Lê a resposta JSON do servidor
                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                     BufferedReader bufferedReader = new BufferedReader(reader)) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
    
                    // Converte JSON para lista de Alunos
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule()); // Suporte para datas
                    List<Aluno> alunos = objectMapper.readValue(response.toString(), 
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Aluno.class));
    
                    // Exibe a lista de alunos
                    if (alunos.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Nenhum aluno cadastrado.", "Lista de Alunos", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        StringBuilder lista = new StringBuilder("Lista de Alunos:\n");
                        for (Aluno aluno : alunos) {
                            lista.append("ID: ").append(aluno.getIdAluno()) // Mostra o ID
                                 .append(", Nome: ").append(aluno.getNome())
                                 .append(", CPF: ").append(aluno.getCpf())
                                 .append(", Idade: ").append(aluno.getIdade())
                                 .append("\n");
                        }
                        JOptionPane.showMessageDialog(null, lista.toString(), "Lista de Alunos", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao buscar lista de alunos! Código de resposta: " + responseCode, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao realizar requisição HTTP: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Função para editar informações do aluno
    private void EditarAlunos() {
        String cpf = JOptionPane.showInputDialog("Digite o CPF do aluno para editar:");

        if (cpf != null && !cpf.trim().isEmpty()) {
            try {
                // GET - Buscar aluno pelo CPF
                ResponseEntity<Aluno> response = alunoController.getAlunoByCpf(cpf); // Retorna ResponseEntity
                Aluno aluno = response.getBody(); // Extrai o objeto Aluno do ResponseEntity

                if (aluno != null) {
                    // Exibe os dados atuais do aluno e permite a edição
                    JTextField nomeInput = new JTextField(aluno.getNome());
                    JTextField telefoneInput = new JTextField(aluno.getTelefone());

                    Object[] message = {
                        "Nome do Aluno:", nomeInput,
                        "Telefone:", telefoneInput
                    };

                    int result = JOptionPane.showConfirmDialog(null, message, "Editar Aluno", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        aluno.setNome(nomeInput.getText().trim());
                        aluno.setTelefone(telefoneInput.getText().trim());

                        // PUT - Atualizar aluno, passando o ID na URL
                        ResponseEntity<Aluno> updateResponse = alunoController.updateAluno(aluno.getIdAluno(), aluno);
                        
                        if (updateResponse.getStatusCode() == HttpStatus.OK) {
                            JOptionPane.showMessageDialog(null, "Aluno atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Erro ao atualizar aluno.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Aluno não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao buscar aluno: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "CPF não informado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
