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
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPagamento;
    private Double valorPago;
    private LocalDate dataPagamento;
    private String status;

    @ManyToOne
    @JoinColumn(name = "id_aluno", nullable = true)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "id_mensalidade", nullable = true)
    private Mensalidade mensalidade;
}
