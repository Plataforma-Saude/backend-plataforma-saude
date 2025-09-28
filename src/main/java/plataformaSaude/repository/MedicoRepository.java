package plataformaSaude.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plataformaSaude.model.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
}
