package kademicsapp.kademicsapp.Service;

import kademicsapp.kademicsapp.Entity.Aluno;
import kademicsapp.kademicsapp.Repository.alunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class alunoService {

    @Autowired
    private alunoRepository alunoRepository;
    
    public List<Aluno> getAllAlunos() {
        return alunoRepository.findAll();
    }

    public Optional<Aluno> getAlunoById(Long id) {
        return alunoRepository.findById(id);
    }

    public Aluno saveAluno(Aluno aluno) {
        return alunoRepository.save(aluno);
    }

    public void deleteAluno(Long id) {
        alunoRepository.deleteById(id);
    }

    // 1. Buscar aluno pelo CPF
    public Aluno findAlunoByCpf(String cpf) {
        Aluno aluno = alunoRepository.findByCpf(cpf);
        if (aluno == null) {
            System.out.println("Nenhum aluno encontrado com CPF: " + cpf);
        }
        return aluno;
    }

    // 2. Atualizar aluno
    public Aluno updateAluno(Long id, Aluno alunoAtualizado) {
        return alunoRepository.findById(id)
                .map(aluno -> {
                    aluno.setNome(alunoAtualizado.getNome());
                    aluno.setTelefone(alunoAtualizado.getTelefone());
                    return alunoRepository.save(aluno);
                })
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }
    
}
