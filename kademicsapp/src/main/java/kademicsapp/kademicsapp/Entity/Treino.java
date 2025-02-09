package kademicsapp.kademicsapp.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTreino;
    private String tipoExercicio;
    private String duracao;

    @ManyToOne
    @JoinColumn(name = "id_aluno", nullable = true)
    @JsonBackReference("aluno-reference")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "id_professor", nullable = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonBackReference("professor-reference")
    private Professor professor;

}
