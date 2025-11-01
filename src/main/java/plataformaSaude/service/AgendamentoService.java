// src/main/java/plataformaSaude/service/AgendamentoService.java
package plataformaSaude.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import plataformaSaude.dto.AgendamentoDTO; // +++ O NOVO DTO
import plataformaSaude.Enum.StatusAgendamento; // +++ IMPORT CORRETO
import plataformaSaude.model.Agendamento;
import plataformaSaude.model.BloqueioHorario; // +++ IMPORT
import plataformaSaude.model.Medico; // +++ IMPORT
import plataformaSaude.model.Paciente; // +++ IMPORT
import plataformaSaude.repository.AgendamentoRepository;
import plataformaSaude.repository.BloqueioHorarioRepository; // +++ IMPORT
import plataformaSaude.repository.MedicoRepository; // +++ IMPORT (Crie se não existir)
import plataformaSaude.repository.PacienteRepository; // +++ IMPORT (Crie se não existir)

import java.time.LocalDateTime;
import java.util.List; // +++ IMPORT
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // +++ NOVAS INJEÇÕES +++
    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private BloqueioHorarioRepository bloqueioHorarioRepository;

    // CREATE - Criar novo agendamento
    public Agendamento criarAgendamento(AgendamentoDTO agendamentoDTO) {

        // 1. Buscar as entidades principais
        Paciente paciente = pacienteRepository.findById(agendamentoDTO.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Medico medico = medicoRepository.findById(agendamentoDTO.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        // 2. Calcular datas início e fim
        LocalDateTime inicio = agendamentoDTO.getDataConsultaInicio();
        int duracao = medico.getDuracaoConsultaMinutos();
        if (duracao <= 0) {
            throw new RuntimeException("Médico não possui duração de consulta configurada.");
        }
        LocalDateTime fim = inicio.plusMinutes(duracao);

        // 3. Validar conflito de horário (a nova lógica)
        if (existeConflitoHorario(null, medico.getId(), inicio, fim)) {
            throw new RuntimeException("Horário indisponível. Já existe um agendamento ou bloqueio neste período.");
        }

        // 4. Criar a entidade
        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(paciente);
        agendamento.setMedico(medico);
        agendamento.setDataConsultaInicio(inicio);
        agendamento.setDataConsultaFim(fim);
        agendamento.setObservacoes(agendamentoDTO.getObservacoes());
        agendamento.setStatus(StatusAgendamento.AGENDADO); // Define o padrão

        return agendamentoRepository.save(agendamento);
    }

    // READ - Listar todos os agendamentos
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
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + id));

        // 1. Buscar entidades
        Paciente paciente = pacienteRepository.findById(agendamentoDTO.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        Medico medico = medicoRepository.findById(agendamentoDTO.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        // 2. Calcular novas datas
        LocalDateTime inicio = agendamentoDTO.getDataConsultaInicio();
        int duracao = medico.getDuracaoConsultaMinutos();
        LocalDateTime fim = inicio.plusMinutes(duracao);

        // 3. Validar conflito (excluindo o ID do próprio agendamento)
        if (existeConflitoHorario(id, medico.getId(), inicio, fim)) {
            throw new RuntimeException("Horário indisponível. Já existe um agendamento ou bloqueio neste período.");
        }

        // 4. Atualizar a entidade
        agendamento.setPaciente(paciente);
        agendamento.setMedico(medico);
        agendamento.setDataConsultaInicio(inicio);
        agendamento.setDataConsultaFim(fim);
        agendamento.setObservacoes(agendamentoDTO.getObservacoes());

        if (agendamentoDTO.getStatus() != null) {
            agendamento.setStatus(agendamentoDTO.getStatus());
        }

        return agendamentoRepository.save(agendamento);
    }

    // DELETE - Deletar agendamento
    public void deletarAgendamento(Long id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new RuntimeException("Agendamento não encontrado com ID: " + id);
        }
        agendamentoRepository.deleteById(id);
    }

    // UPDATE - Atualizar status do agendamento
    public Agendamento atualizarStatus(Long id, StatusAgendamento novoStatus) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + id));

        agendamento.setStatus(novoStatus);
        return agendamentoRepository.save(agendamento);
    }

    // --- Buscas (agora usando os métodos corretos do repo) ---

    public List<Agendamento> buscarPorPaciente(Long pacienteId) {
        return agendamentoRepository.findByPacienteId(pacienteId);
    }

    public List<Agendamento> buscarPorMedico(Long medicoId) {
        return agendamentoRepository.findByMedicoId(medicoId);
    }

    public List<Agendamento> buscarPorEspecialidade(String especialidade) {
        return agendamentoRepository.findByMedicoEspecialidade(especialidade);
    }

    public List<Agendamento> buscarPorStatus(StatusAgendamento status) {
        return agendamentoRepository.findByStatus(status);
    }

    // --- Métodos auxiliares ---

    /**
     * Nova lógica de conflito. Verifica SOBREPOSIÇÃO com
     * Agendamentos E Bloqueios.
     */
    private boolean existeConflitoHorario(Long idExcluir, Long medicoId, LocalDateTime inicio, LocalDateTime fim) {

        // 1. Verifica conflito com AGENDAMENTOS
        List<Agendamento> agendamentosConflitantes = agendamentoRepository.findOverlappingAppointments(
                medicoId, inicio, fim, (idExcluir != null ? idExcluir : -1L)
        );

        if (!agendamentosConflitantes.isEmpty()) {
            return true; // Conflito com outro agendamento
        }

        // 2. Verifica conflito com BLOQUEIOS (Férias, Almoço...)
        // (Precisamos adicionar a query no BloqueioHorarioRepository)
        List<BloqueioHorario> bloqueiosConflitantes = bloqueioHorarioRepository.findOverlappingBlocks(medicoId, inicio, fim);

        return !bloqueiosConflitantes.isEmpty(); // Conflito com bloqueio
    }

    // Os métodos convertToEntity e updateEntityFromDTO não são mais necessários
    // pois a lógica agora está dentro de criar/atualizar.
}