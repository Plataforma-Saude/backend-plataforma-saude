package plataformaSaude.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import plataformaSaude.Enum.StatusAgendamento;
import plataformaSaude.model.Agendamento;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByNomePacienteContainingIgnoreCase(String nomePaciente);

    List<Agendamento> findByNomeMedicoContainingIgnoreCase(String nomeMedico);

    List<Agendamento> findByEspecialidadeContainingIgnoreCase(String especialidade);

    List<Agendamento> findByStatus(StatusAgendamento status);

    List<Agendamento> findByDataConsultaBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT a FROM Agendamento a WHERE a.dataConsulta >= :dataAtual ORDER BY a.dataConsulta ASC")
    List<Agendamento> findAgendamentosFuturos(@Param("dataAtual") LocalDateTime dataAtual);

    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.nomeMedico = :medico AND a.dataConsulta = :dataConsulta AND a.id != :idExcluir")
    Long countByMedicoAndDataConsulta(@Param("medico") String medico,
                                      @Param("dataConsulta") LocalDateTime dataConsulta,
                                      @Param("idExcluir") Long idExcluir);
}