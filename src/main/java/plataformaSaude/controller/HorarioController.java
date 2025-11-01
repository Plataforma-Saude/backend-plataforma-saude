// src/main/java/plataformaSaude/controller/HorarioController.java
package plataformaSaude.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.model.Horario;
import plataformaSaude.repository.HorarioRepository;

// +++ NOVOS IMPORTS +++
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import plataformaSaude.service.HorarioService; // O serviço que criamos
import java.time.LocalDate;
import java.time.LocalTime;
// --- FIM DOS NOVOS IMPORTS ---

import java.util.List;

@RestController
@RequestMapping("/horarios") // Vamos manter seu path base /horarios
@CrossOrigin(origins = "*") // Adicionando CrossOrigin que você usou no outro controller
public class HorarioController {

    // Este repository é para o CRUD de Janelas (Admin)
    @Autowired
    private HorarioRepository horarioRepository;

    // +++ NOVO SERVIÇO +++
    // Este serviço é para a lógica de Slots (Paciente)
    @Autowired
    private HorarioService horarioService;

    // --- Endpoints de CRUD do Admin (Seu código original) ---

    @PostMapping
    public Horario criar(@RequestBody Horario horario) {
        // TODO: Aqui você deve associar o Horario a um Medico
        // ex: horario.setMedico(medicoService.findById(horarioDTO.getMedicoId()));
        return horarioRepository.save(horario);
    }

    @GetMapping
    public List<Horario> listar() {
        return horarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public Horario buscarPorId(@PathVariable Long id) {
        return horarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Horário não encontrado"));
    }

    @PutMapping("/{id}")
    public Horario atualizar(@PathVariable Long id, @RequestBody Horario horario) {
        Horario existente = horarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Horário não encontrado"));
        existente.setDescricao(horario.getDescricao());
        existente.setDiaSemana(horario.getDiaSemana());
        existente.setHoraInicio(horario.getHoraInicio());
        existente.setHoraFim(horario.getHoraFim());
        // TODO: Atualizar o médico associado, se necessário
        return horarioRepository.save(existente);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        horarioRepository.deleteById(id);
    }


    // +++ ENDPOINT PARA O PACIENTE +++

    /**
     * Endpoint para o frontend buscar os horários livres (slots).
     *
     * Exemplo de chamada:
     * GET /horarios/disponiveis/1?data=2025-11-20
     *
     * @param medicoId O ID do médico
     * @param data A data (no formato AAAA-MM-DD)
     * @return Uma lista de LocalTime (ex: ["09:00", "09:15", "10:30"])
     */
    @GetMapping("/disponiveis/{medicoId}")
    public ResponseEntity<List<LocalTime>> getSlotsDisponiveis(
            @PathVariable Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        try {
            List<LocalTime> slots = horarioService.encontrarSlotsDisponiveis(medicoId, data);
            return ResponseEntity.ok(slots);
        } catch (RuntimeException e) {
            // Se o médico não for encontrado, por exemplo
            return ResponseEntity.notFound().build();
        }
    }
}