package plataformaSaude.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Medico extends Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String crm;
    private String especialidade;
    private String foto;

    public Medico() {
        super();
    }

    public Medico(String nome, String sobrenome, String cpf, String email, String senha,
                  String celular, LocalDate dataNascimento, String crm, String especialidade, 
                  String foto) {
        super(nome, sobrenome, cpf, email, senha, celular, dataNascimento);
        this.crm = crm;
        this.especialidade = especialidade;
        this.foto = foto != null ? foto : null;
    }

    public Long getId() {
        return id;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
