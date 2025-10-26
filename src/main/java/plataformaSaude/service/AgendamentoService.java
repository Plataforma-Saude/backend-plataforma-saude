package plataformaSaude.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import plataformaSaude.model.Agendamento;
import plataformaSaude.repository.AgendamentoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // CREATE - Criar novo agendamento
    public Agendamento criarAgendamento(AgendamentoDTO agendamentoDTO) {
        // Validar conflito de horário
        if (existeConflitoHorario(null, agendamentoDTO.getNomeMedico(), agendamentoDTO.getDataConsulta())) {
            throw new RuntimeException("Médico já possui consulta agendada para este horário");
        }

        Agendamento agendamento = convertToEntity(agendamentoDTO);
        return agendamentoRepository.save(agendamento);
    }

    // READ - Buscar todos os agendamentos
    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    // READ - Buscar agendamento por ID
    public Optional<Agendamento> buscarPorId(Long id) {
        return agendamentoRepository.findById(id);
    }

    // READ - Buscar agendamentos futuros
    public List<Agendamento> listarFuturos() {
        return agendamentoRepository.findAgendamentosFuturos(LocalDateTime.now());
    }

    // UPDATE - Atualizar agendamento
    public Agendamento atualizarAgendamento(Long id, AgendamentoDTO agendamentoDTO) {
        Optional<Agendamento> agendamentoExistente = agendamentoRepository.findById(id);

        if (agendamentoExistente.isPresent()) {
            // Validar conflito de horário (excluindo o próprio agendamento)
            if (existeConflitoHorario(id, agendamentoDTO.getNomeMedico(), agendamentoDTO.getDataConsulta())) {
                throw new RuntimeException("Médico já possui consulta agendada para este horário");
            }

            Agendamento agendamento = agendamentoExistente.get();
            updateEntityFromDTO(agendamento, agendamentoDTO);
            return agendamentoRepository.save(agendamento);
        }

        throw new RuntimeException("Agendamento não encontrado com ID: " + id);
    }

    // DELETE - Deletar agendamento
    public void deletarAgendamento(Long id) {
        if (agendamentoRepository.existsById(id)) {
            agendamentoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Agendamento não encontrado com ID: " + id);
        }
    }

    // UPDATE - Atualizar status do agendamento
    public Agendamento atualizarStatus(Long id, StatusAgendamento novoStatus) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id);

        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            agendamento.setStatus(novoStatus);
            return agendamentoRepository.save(agendamento);
        }

        throw new RuntimeException("Agendamento não encontrado com ID: " + id);
    }

    // Buscar por diversos critérios
    public List<Agendamento> buscarPorPaciente(String nomePaciente) {
        return agendamentoRepository.findByNomePacienteContainingIgnoreCase(nomePaciente);
    }

    public List<Agendamento> buscarPorMedico(String nomeMedico) {
        return agendamentoRepository.findByNomeMedicoContainingIgnoreCase(nomeMedico);
    }

    public List<Agendamento> buscarPorEspecialidade(String especialidade) {
        return agendamentoRepository.findByEspecialidadeContainingIgnoreCase(especialidade);
    }

    public List<Agendamento> buscarPorStatus(StatusAgendamento status) {
        return agendamentoRepository.findByStatus(status);
    }

    // Métodos auxiliares
    private boolean existeConflitoHorario(Long idExcluir, String medico, LocalDateTime dataConsulta) {
        return agendamentoRepository.countByMedicoAndDataConsulta(medico, dataConsulta, idExcluir != null ? idExcluir : -1L) > 0;
    }

    private Agendamento convertToEntity(AgendamentoDTO dto) {
        Agendamento agendamento = new Agendamento();
        updateEntityFromDTO(agendamento, dto);
        return agendamento;
    }

    private void updateEntityFromDTO(Agendamento agendamento, AgendamentoDTO dto) {
        agendamento.setNomePaciente(dto.getNomePaciente());
        agendamento.setEmail(dto.getEmail());
        agendamento.setTelefone(dto.getTelefone());
        agendamento.setEspecialidade(dto.getEspecialidade());
        agendamento.setNomeMedico(dto.getNomeMedico());
        agendamento.setDataConsulta(dto.getDataConsulta());
        agendamento.setObservacoes(dto.getObservacoes());

        if (dto.getStatus() != null) {
            agendamento.setStatus(dto.getStatus());
        }
    }
}
