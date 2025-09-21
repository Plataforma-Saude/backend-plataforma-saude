package plataformaSaude.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plataformaSaude.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}

