// src/main/java/plataformaSaude/controller/AgendamentoController.java
package plataformaSaude.controller;

import plataformaSaude.dto.AgendamentoDTO; // +++ O NOVO DTO
import plataformaSaude.model.Agendamento;
import plataformaSaude.Enum.StatusAgendamento; // +++ IMPORT CORRETO
import plataformaSaude.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    // CREATE - Criar novo agendamento
    @PostMapping
    public ResponseEntity<?> criarAgendamento(@Valid @RequestBody AgendamentoDTO agendamentoDTO) { // +++ DTO ATUALIZADO
        try {
            Agendamento agendamento = agendamentoService.criarAgendamento(agendamentoDTO);
            // Retornar o agendamento completo (com dados do médico/paciente)
            return ResponseEntity.status(HttpStatus.CREATED).body(agendamento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // READ - Listar todos os agendamentos
    @GetMapping
    public ResponseEntity<List<Agendamento>> listarTodos() {
        List<Agendamento> agendamentos = agendamentoService.listarTodos();
        return ResponseEntity.ok(agendamentos);
    }

    // READ - Buscar agendamento por ID
    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable Long id) {
        Optional<Agendamento> agendamento = agendamentoService.buscarPorId(id);
        return agendamento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // READ - Listar agendamentos futuros
    @GetMapping("/futuros")
    public ResponseEntity<List<Agendamento>> listarFuturos() {
        List<Agendamento> agendamentos = agendamentoService.listarFuturos();
        return ResponseEntity.ok(agendamentos);
    }

    // UPDATE - Atualizar agendamento
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAgendamento(@PathVariable Long id,
                                                  @Valid @RequestBody AgendamentoDTO agendamentoDTO) { // +++ DTO ATUALIZADO
        try {
            Agendamento agendamento = agendamentoService.atualizarAgendamento(id, agendamentoDTO);
            return ResponseEntity.ok(agendamento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UPDATE - Atualizar status do agendamento
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id,
                                             @RequestParam StatusAgendamento status) {
        try {
            Agendamento agendamento = agendamentoService.atualizarStatus(id, status);
            return ResponseEntity.ok(agendamento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE - Deletar agendamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable Long id) {
        try {
            agendamentoService.deletarAgendamento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Buscas específicas (Refatoradas) ---

    // Agora busca por ID, não por nome
    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<Agendamento>> buscarPorPaciente(@PathVariable Long id) {
        List<Agendamento> agendamentos = agendamentoService.buscarPorPaciente(id);
        return ResponseEntity.ok(agendamentos);
    }

    // Agora busca por ID, não por nome
    @GetMapping("/medico/{id}")
    public ResponseEntity<List<Agendamento>> buscarPorMedico(@PathVariable Long id) {
        List<Agendamento> agendamentos = agendamentoService.buscarPorMedico(id);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/especialidade/{especialidade}")
    public ResponseEntity<List<Agendamento>> buscarPorEspecialidade(@PathVariable String especialidade) {
        List<Agendamento> agendamentos = agendamentoService.buscarPorEspecialidade(especialidade);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Agendamento>> buscarPorStatus(@PathVariable StatusAgendamento status) {
        List<Agendamento> agendamentos = agendamentoService.buscarPorStatus(status);
        return ResponseEntity.ok(agendamentos);
    }
}