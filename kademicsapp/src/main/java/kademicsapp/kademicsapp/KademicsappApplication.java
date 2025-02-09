package kademicsapp.kademicsapp;

import javax.swing.JOptionPane;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import kademicsapp.kademicsapp.View.viewManager;

@SpringBootApplication(scanBasePackages = "kademicsapp.kademicsapp")
public class KademicsappApplication implements CommandLineRunner {


    public static void main(String[] args) {
        // Garantir que o programa rode em modo gráfico
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(KademicsappApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        JOptionPane.showMessageDialog(null, "Bem-vindo! Você está no aplicativo Kademics", "Kademics App", JOptionPane.INFORMATION_MESSAGE);
        viewManager viewManager = new viewManager();
        boolean running = true;

        while (running) {
            int menuInput = 0;
            String[] menu = { "Aluno", "Professor", "Treino", "Mensalidade", "Sair" };
            menuInput = JOptionPane.showOptionDialog(
                null, 
                "Escolha uma opção", 
                "Kademics App", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                menu, 
                menu[0]
            );

            switch (menuInput) {
                case 0:
                    viewManager.showAlunoView();
                    break;
                case 1:
                    viewManager.showProfessorView();
                    break;
                case 2:
                    viewManager.showTreinoView();
                    break;
                case 3:
                    viewManager.showMensalidadeView();
                    break;
                case 4:
                    running = false; 
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida! Tente novamente.");
            }
        }

        System.exit(0); 
    }
}