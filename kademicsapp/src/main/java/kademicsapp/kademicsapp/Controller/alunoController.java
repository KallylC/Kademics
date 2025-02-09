package kademicsapp.kademicsapp.Controller;

import kademicsapp.kademicsapp.Entity.Aluno;
import kademicsapp.kademicsapp.Entity.Mensalidade;
import kademicsapp.kademicsapp.Service.alunoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.swing.JOptionPane;

@RestController
@RequestMapping("/api/alunos")
public class alunoController {

    @Autowired
    private alunoService alunoService;

    @PostMapping
    public ResponseEntity<Aluno> addAluno(@RequestBody Aluno aluno) {
        try {
            System.out.println("Recebendo dados: " + aluno);
            Aluno savedAluno = alunoService.saveAluno(aluno);
            System.out.println("Aluno salvo: " + savedAluno);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAluno);
        } catch (Exception e) {
            System.out.println("Erro ao salvar aluno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping
        public ResponseEntity<List<Aluno>> getAllAlunos() {
        List<Aluno> alunos = alunoService.getAllAlunos();
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluno> getAlunoById(@PathVariable Long id) {
        return alunoService.getAlunoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Aluno> getAlunoByCpf(@PathVariable String cpf) {
        System.out.println("CPF recebido: " + cpf);
        Aluno aluno = alunoService.findAlunoByCpf(cpf);
        if (aluno != null) {
            System.out.println("Aluno encontrado: " + aluno.getNome());
            return ResponseEntity.ok(aluno);
        } else {
            System.out.println("Aluno não encontrado para CPF: " + cpf);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 2. PUT - Atualizar os dados do aluno
    @PutMapping("/alunos/{id}")
    public ResponseEntity<Aluno> updateAluno(@PathVariable Long id, @RequestBody Aluno alunoAtualizado) {
        try {
            // Chama o serviço para atualizar o aluno
            Aluno aluno = alunoService.updateAluno(id, alunoAtualizado);
            return ResponseEntity.ok(aluno); // Retorna o aluno atualizado com status 200 OK
        } catch (RuntimeException e) {
            // Caso o aluno não seja encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // Caso ocorra outro tipo de erro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAluno(@PathVariable Long id) {
        alunoService.deleteAluno(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/alunos")
    public String listarAlunos() {
        return "Lista de Alunos";
    }
}
