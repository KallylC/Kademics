package kademicsapp.kademicsapp.Service;

import kademicsapp.kademicsapp.Entity.Mensalidade;
import kademicsapp.kademicsapp.Repository.mensalidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class mensalidadeService {

    @Autowired
    private mensalidadeRepository mensalidadeRepository;

    public List<Mensalidade> getAllMensalidades() {
        return mensalidadeRepository.findAll();
    }

    public Optional<Mensalidade> getMensalidadeById(Long id) {
        return mensalidadeRepository.findById(id);
    }

    public Mensalidade saveMensalidade(Mensalidade mensalidade) {
        return mensalidadeRepository.save(mensalidade);
    }

    public void deleteMensalidade(Long id) {
        mensalidadeRepository.deleteById(id);
    }
}
