// src/main/java/plataformaSaude/dto/MedicoDTO.java
package plataformaSaude.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para o Admin criar ou atualizar um Medico.
 * Inclui dados do Usuario, dados do Medico e a grade de horários.
 */
public class MedicoDTO {

    // Dados do Usuario
    private String nome;
    private String sobrenome;
    private String cpf;
    private String email;
    private String senha; // Apenas para criação
    private String celular;
    private LocalDate dataNascimento;

    // Dados do Medico
    private String crm;
    private String especialidade;
    private String foto; // URL da foto
    private int duracaoConsultaMinutos; // O novo campo!

    // A nova grade de horários
    private List<HorarioDTO> horariosTrabalho;

    // Getters e Setters para todos os campos
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
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public int getDuracaoConsultaMinutos() { return duracaoConsultaMinutos; }
    public void setDuracaoConsultaMinutos(int duracaoConsultaMinutos) { this.duracaoConsultaMinutos = duracaoConsultaMinutos; }
    public List<HorarioDTO> getHorariosTrabalho() { return horariosTrabalho; }
    public void setHorariosTrabalho(List<HorarioDTO> horariosTrabalho) { this.horariosTrabalho = horariosTrabalho; }
}