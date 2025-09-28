package plataformaSaude.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plataformaSaude.model.Horario;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
}