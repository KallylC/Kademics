package kademicsapp.kademicsapp.Repository;

import kademicsapp.kademicsapp.Entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface alunoRepository extends JpaRepository<Aluno, Long> {
    Aluno findByCpf(String cpf);
    Aluno findByNome(String nome);

    
}
