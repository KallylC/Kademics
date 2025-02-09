package kademicsapp.kademicsapp.Repository;

import kademicsapp.kademicsapp.Entity.Treino;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface treinoRepository extends JpaRepository<Treino, Long> {
     @Query("SELECT t FROM Treino t LEFT JOIN FETCH t.professor LEFT JOIN FETCH t.aluno")
    List<Treino> findAllWithRelations();
}
