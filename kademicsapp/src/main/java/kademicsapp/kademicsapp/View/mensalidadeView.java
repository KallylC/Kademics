package kademicsapp.kademicsapp.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kademicsapp.kademicsapp.Entity.Mensalidade;

public class mensalidadeView {

    public void mensalidadeMenu() {
        int menuInput = 0;
        String[] menu = { "Efetuar Pagamento", "Status Mensalidade", "Sair" };

        menuInput = JOptionPane.showOptionDialog(
                null,
                "Escolha uma opção:",
                "Kademics - Menu Mensalidade",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                menu,
                menu[0]);

        switch (menuInput) {
            case 0:
                efetuarPagamento();
                break;
            case 1:
                verMensalidade();
                break;
            case 2:
                break;
            default:
                JOptionPane.showMessageDialog(null, "Nenhuma opção selecionada.");
                break;
        }
    }

    private void efetuarPagamento() {
        JTextField idMensalidadeInput = new JTextField();
        JTextField valorPagoInput = new JTextField();

        Object[] message = {
                "ID da Mensalidade:", idMensalidadeInput,
                "Valor Pago:", valorPagoInput
        };

        int result = JOptionPane.showConfirmDialog(null, message, "Efetuar Pagamento",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Long idMensalidade = Long.parseLong(idMensalidadeInput.getText().trim());
                Double valorPago = Double.parseDouble(valorPagoInput.getText().trim());

                if (idMensalidade <= 0 || valorPago <= 0) {
                    JOptionPane.showMessageDialog(null, "ID e Valor Pago devem ser válidos.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Mensalidade mensalidade = new Mensalidade();
                mensalidade.setIdMensalidade(idMensalidade);
                mensalidade.setValor(valorPago);

                enviarPagamento(mensalidade);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao efetuar pagamento: " + e.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void enviarPagamento(Mensalidade mensalidade) {
        try {
            URL url = new URL("http://localhost:8080/api/mensalidades/pagamento");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String jsonInputString = objectMapper.writeValueAsString(mensalidade);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonInputString.getBytes("utf-8"));
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                JOptionPane.showMessageDialog(null, "Pagamento efetuado com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao efetuar pagamento. Código: " + responseCode, "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro na requisição: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verMensalidade() {
        JTextField idMensalidadeInput = new JTextField();

        Object[] message = {
                "ID da Mensalidade:", idMensalidadeInput
        };

        int result = JOptionPane.showConfirmDialog(null, message, "Ver Status da Mensalidade",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Long idMensalidade = Long.parseLong(idMensalidadeInput.getText().trim());

                if (idMensalidade <= 0) {
                    JOptionPane.showMessageDialog(null, "ID da mensalidade deve ser válido.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                buscarStatusMensalidade(idMensalidade);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao consultar status: " + e.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarStatusMensalidade(Long idMensalidade) {
        try {
            URL url = new URL("http://localhost:8080/api/mensalidades/" + idMensalidade);
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
                    Mensalidade mensalidade = objectMapper.readValue(response.toString(), Mensalidade.class);

                    String status = mensalidade.isPago() ? "Pago" : "Pendente";
                    JOptionPane.showMessageDialog(null, "Status da Mensalidade (ID: " + idMensalidade + "): " + status,
                            "Status Mensalidade", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao buscar status. Código: " + responseCode, "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro na requisição: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
