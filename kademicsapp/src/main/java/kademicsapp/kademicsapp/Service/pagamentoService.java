package kademicsapp.kademicsapp.Service;

import kademicsapp.kademicsapp.Entity.Pagamento;
import kademicsapp.kademicsapp.Repository.pagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class pagamentoService {

    @Autowired
    private pagamentoRepository pagamentoRepository;

    public List<Pagamento> getAllPagamentos() {
        return pagamentoRepository.findAll();
    }

    public Optional<Pagamento> getPagamentoById(Long id) {
        return pagamentoRepository.findById(id);
    }

    public Pagamento savePagamento(Pagamento pagamento) {
        return pagamentoRepository.save(pagamento);
    }

    public void deletePagamento(Long id) {
        pagamentoRepository.deleteById(id);
    }
}
