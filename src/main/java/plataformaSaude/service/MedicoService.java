// src/main/java/plataformaSaude/service/MedicoService.java
package plataformaSaude.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // IMPORTANTE
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plataformaSaude.dto.HorarioDTO;
import plataformaSaude.dto.MedicoDTO;
import plataformaSaude.model.Horario;
import plataformaSaude.model.Medico;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.HorarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private HorarioRepository horarioRepository; // Usado na atualização

    @Autowired
    private PasswordEncoder passwordEncoder; // Injete seu codificador de senha

    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    public Optional<Medico> buscarPorId(Long id) {
        return medicoRepository.findById(id);
    }

    @Transactional // Garante que tudo (Médico e Horários) seja salvo junto
    public Medico criarMedico(MedicoDTO dto) {

        Medico medico = new Medico();

        // Copia dados do Usuario + Medico
        medico.setNomeCompleto(dto.getNome());
        medico.setCpf(dto.getCpf());
        medico.setEmail(dto.getEmail());
        medico.setSenha(passwordEncoder.encode(dto.getSenha())); // Codifica a senha
        medico.setCelular(dto.getCelular());
        medico.setDataNascimento(dto.getDataNascimento());
        medico.setCrm(dto.getCrm());
        medico.setEspecialidade(dto.getEspecialidade());
        medico.setFoto(dto.getFoto());
        medico.setDuracaoConsultaMinutos(dto.getDuracaoConsultaMinutos());

        // Converte os HorarioDTOs em Entidades Horario
        List<Horario> horarios = new ArrayList<>();
        if (dto.getHorariosTrabalho() != null) {
            for (HorarioDTO horarioDTO : dto.getHorariosTrabalho()) {
                Horario horario = new Horario();
                horario.setDescricao(horarioDTO.getDescricao());
                horario.setDiaSemana(horarioDTO.getDiaSemana());
                horario.setHoraInicio(horarioDTO.getHoraInicio());
                horario.setHoraFim(horarioDTO.getHoraFim());
                horario.setMedico(medico); // +++ LIGAÇÃO IMPORTANTE +++
                horarios.add(horario);
            }
        }
        medico.setHorariosTrabalho(horarios);

        // Salva o médico. O CascadeType.ALL salvará os horários juntos.
        return medicoRepository.save(medico);
    }

    @Transactional
    public Medico atualizarMedico(Long id, MedicoDTO dto) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado com ID: " + id));

        // Atualiza dados (exceto senha, a menos que haja lógica para isso)
        medico.setNomeCompleto(dto.getNome());
        medico.setCpf(dto.getCpf());
        medico.setEmail(dto.getEmail());
        medico.setCelular(dto.getCelular());
        medico.setDataNascimento(dto.getDataNascimento());
        medico.setCrm(dto.getCrm());
        medico.setEspecialidade(dto.getEspecialidade());
        medico.setFoto(dto.getFoto());
        medico.setDuracaoConsultaMinutos(dto.getDuracaoConsultaMinutos());

        // Lógica de atualização de horários:
        // A forma mais fácil é remover os antigos e adicionar os novos.
        // (Graças ao orphanRemoval=true no @OneToMany do Medico)
        medico.getHorariosTrabalho().clear();

        if (dto.getHorariosTrabalho() != null) {
            for (HorarioDTO horarioDTO : dto.getHorariosTrabalho()) {
                Horario horario = new Horario();
                horario.setDescricao(horarioDTO.getDescricao());
                horario.setDiaSemana(horarioDTO.getDiaSemana());
                horario.setHoraInicio(horarioDTO.getHoraInicio());
                horario.setHoraFim(horarioDTO.getHoraFim());
                horario.setMedico(medico); // Liga o novo horário ao médico
                medico.getHorariosTrabalho().add(horario);
            }
        }

        return medicoRepository.save(medico);
    }

    public void deletarMedico(Long id) {
        if (!medicoRepository.existsById(id)) {
            throw new RuntimeException("Médico não encontrado com ID: " + id);
        }
        medicoRepository.deleteById(id);
    }
}