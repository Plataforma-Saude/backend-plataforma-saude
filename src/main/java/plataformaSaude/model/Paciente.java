package plataformaSaude.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "pacientes")
public class Paciente extends Usuario {

    private String profissao;

    public Paciente() {
        super();
    }

    public Paciente(String nome, String sobrenome, String cpf, String email, String senha,
                    String celular, LocalDate dataNascimento, String profissao) {
        super(nome, sobrenome, cpf, email, senha, celular, dataNascimento);
        this.profissao = profissao;
    }

    public String getProfissao() { return profissao; }
    public void setProfissao(String profissao) { this.profissao = profissao; }
}
