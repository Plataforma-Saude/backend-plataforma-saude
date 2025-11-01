package plataformaSaude.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "medicos")
public class Medico extends Usuario {

    private String crm;
    private String especialidade;
    private String foto;

    public Medico() {
        super();
    }

    public Medico(String nomeCompleto, String cpf, String email, String senha,
                  String celular, LocalDate dataNascimento, String crm, String especialidade,
                  String foto) {
        super(nomeCompleto, cpf, email, senha, celular, dataNascimento);
        this.crm = crm;
        this.especialidade = especialidade;
        this.foto = foto;
    }

    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
