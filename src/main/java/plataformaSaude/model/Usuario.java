package plataformaSaude.model;
import java.time.LocalDate;
import jakarta.persistence.MappedSuperclass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // Import necessário

import java.time.LocalDate;
import java.util.Collection;
import java.util.List; // Import necessário


//Adicionei essa linha, pois não estava herdando as propriedades de usuário (invocando o super/constructor) em médico e paciente
import jakarta.persistence.MappedSuperclass;


@MappedSuperclass
public class Usuario implements UserDetails {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Define as permissões do usuário. Para começar, todos terão a permissão 'USER'.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.senha; // Spring Security usará este metodo para pegar a senha.
    }

    @Override
    public String getUsername() {
        return this.email; // Usaremos o email como "username" para o login.
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // A conta nunca expira.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // A conta nunca é bloqueada.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // As credenciais nunca expiram.
    }

    @Override
    public boolean isEnabled() {
        return true; // A conta está sempre ativa.
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