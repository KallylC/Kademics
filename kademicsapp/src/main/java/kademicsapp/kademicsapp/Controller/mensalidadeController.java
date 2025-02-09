package kademicsapp.kademicsapp.Controller;

import kademicsapp.kademicsapp.Entity.Mensalidade;
import kademicsapp.kademicsapp.Service.mensalidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensalidades")
public class mensalidadeController {

    @Autowired
    private mensalidadeService mensalidadeService;

    @GetMapping
    public List<Mensalidade> getAllMensalidades() {
        return mensalidadeService.getAllMensalidades();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mensalidade> getMensalidadeById(@PathVariable Long id) {
        return mensalidadeService.getMensalidadeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mensalidade saveMensalidade(@RequestBody Mensalidade mensalidade) {
        return mensalidadeService.saveMensalidade(mensalidade);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMensalidade(@PathVariable Long id) {
        mensalidadeService.deleteMensalidade(id);
        return ResponseEntity.noContent().build();
    }
}
