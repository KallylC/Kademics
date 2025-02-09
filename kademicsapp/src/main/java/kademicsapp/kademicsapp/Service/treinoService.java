package kademicsapp.kademicsapp.Service;

import kademicsapp.kademicsapp.Entity.Treino;
import kademicsapp.kademicsapp.Repository.treinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class treinoService {

    @Autowired
    private treinoRepository treinoRepository;

    public List<Treino> getAllTreinos() {
        return treinoRepository.findAllWithRelations();
    }   

    public Optional<Treino> getTreinoById(Long id) {
        return treinoRepository.findById(id);
    }

    public Treino saveTreino(Treino treino) {
        return treinoRepository.save(treino);
    }

    public void deleteTreino(Long id) {
        treinoRepository.deleteById(id);
    }
}
