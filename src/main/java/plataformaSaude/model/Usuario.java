package plataformaSaude.model;
import java.time.LocalDate;

@MappedSuperclass
public class Usuario {
    private String nome;
    private String sobrenome;
    private String cpf;
    private String email;
    private String senha;
    private String telefone;
    private String celular;
    private String cep;
    private String rua;
    private String cidade;
    private String estado;
    private LocalDate dataNascimento;
    private LocalDate dataCadastro;

      //Sugestão para dados que são obrigatórios no cadastro, os demais dados poderiam ser preenchidos depois!!
    public Usuario(String nome, String sobrenome, String cpf, String email, String senha, String celular, LocalDate dataNascimento) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.celular = celular;
        this.dataNascimento = dataNascimento;
        this.dataCadastro = LocalDate.now();
    }

    public Usuario() {
        this.dataCadastro = LocalDate.now();
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public LocalDate getDataCadastro() { return dataCadastro; }
}