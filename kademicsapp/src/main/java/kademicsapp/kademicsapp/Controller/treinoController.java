package kademicsapp.kademicsapp.Controller;

import kademicsapp.kademicsapp.Entity.Aluno;
import kademicsapp.kademicsapp.Entity.Professor;
import kademicsapp.kademicsapp.Entity.Treino;
import kademicsapp.kademicsapp.Repository.alunoRepository;
import kademicsapp.kademicsapp.Repository.professorRepository;
import kademicsapp.kademicsapp.Service.treinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/treinos")
public class treinoController {

    @Autowired
    private treinoService treinoService;
    @Autowired
    private professorRepository professorRepository;
    @Autowired
    private alunoRepository alunoRepository;

    @GetMapping
    public List<Treino> getAllTreinos() {
        return treinoService.getAllTreinos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Treino> getTreinoById(@PathVariable Long id) {
        return treinoService.getTreinoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Treino> criarTreino(@RequestBody Treino treino) {
        try {
            // Atribuindo valores para os campos necessários. id_aluno e id_professor podem ser nulos
            if (treino.getTipoExercicio() == null || treino.getDuracao() == null) {
                return ResponseEntity.badRequest().body(null);  // Retorna erro se campos obrigatórios não forem preenchidos
            }
            
            // Salvar o treino sem id_aluno e id_professor
            treino.setAluno(null);  // Garante que o id_aluno e id_professor não são enviados
            treino.setProfessor(null);  // Pode ser nulo até que o treino seja atribuído
            
            Treino treinoSalvo = treinoService.saveTreino(treino);  // Salvando no banco de dados
            return ResponseEntity.status(HttpStatus.CREATED).body(treinoSalvo);  // Retorna o treino salvo
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Retorna erro em caso de falha
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTreino(@PathVariable Long id) {
        treinoService.deleteTreino(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarTreino(@PathVariable Long id, @RequestBody Treino treinoAtualizado) {
        try {
            Treino treinoExistente = treinoService.getTreinoById(id)
                .orElseThrow(() -> new RuntimeException("Treino não encontrado"));

            if (treinoAtualizado.getProfessor() != null) {
                treinoExistente.setProfessor(
                    new Professor(treinoAtualizado.getProfessor().getIdProfessor())
                );
            }

            if (treinoAtualizado.getAluno() != null) {
                treinoExistente.setAluno(
                    new Aluno(treinoAtualizado.getAluno().getIdAluno())
                );
            }

            Treino treinoSalvo = treinoService.saveTreino(treinoExistente);
            return ResponseEntity.ok(treinoSalvo);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno");
        }
    }


    @PostMapping("/util/buscarIds")
    public ResponseEntity<Map<String, Long>> buscarIds(@RequestBody List<String> nomes) {
        Map<String, Long> ids = new HashMap<>();
        for (String nome : nomes) {
            Professor professor = professorRepository.findByNome(nome);
            Aluno aluno = alunoRepository.findByNome(nome);

            if (professor != null) {
                ids.put(nome, professor.getIdProfessor());
            } else if (aluno != null) {
                ids.put(nome, aluno.getIdAluno());
            }
        }
        return ResponseEntity.ok(ids);
    }
}
