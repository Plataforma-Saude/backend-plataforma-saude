package estruturaDoProjeto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import estruturaDoProjeto.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}

