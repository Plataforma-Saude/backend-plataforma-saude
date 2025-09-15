package estruturaDoProjeto;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Paciente extends Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String profissao;

    public Paciente() {
        super();
    }

    public Paciente(String nome, String sobrenome, String cpf, String email, String senha,
                    String celular, LocalDate dataNascimento, String profissao) {
        super(nome, sobrenome, cpf, email, senha, celular, dataNascimento);
        this.profissao = profissao;
    }

    public Long getId() {
        return id;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }
}
