package kademicsapp.kademicsapp.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mensalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMensalidade;
    private Double valor;
    private LocalDate dataVencimento;

    @ManyToOne
    @JoinColumn(name = "id_aluno", nullable = true)
    private Aluno aluno;

    public boolean isPago() {
        throw new UnsupportedOperationException("Unimplemented method 'isPago'");
    }
}
