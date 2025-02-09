package kademicsapp.kademicsapp.Repository;

import kademicsapp.kademicsapp.Entity.Professor;


import org.springframework.data.jpa.repository.JpaRepository;

public interface professorRepository extends JpaRepository<Professor, Long> {
    Professor findByNome(String nome);
}