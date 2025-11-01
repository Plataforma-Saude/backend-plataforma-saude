// src/main/java/plataformaSaude/controller/MedicoController.java
package plataformaSaude.controller;

import jakarta.validation.Valid; // +++ IMPORT
import org.springframework.beans.factory.annotation.Autowired; // +++ IMPORT
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.dto.MedicoDTO; // +++ IMPORT (Usando DTO)
import plataformaSaude.model.Medico;
// --- REPOSITORY REMOVIDO ---
// import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.service.MedicoService; // +++ IMPORT (Usando Service)

// --- MAPS REMOVIDOS ---
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicos") // Mudei para /api/medicos por consistência
@CrossOrigin(origins = "*") // +++ Adicionado
public class MedicoController {

    // --- MUDANÇA DE DEPENDÊNCIA ---
    private final MedicoService medicoService;

    @Autowired // +++ Adicionado
    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }
    // --- FIM DA MUDANÇA ---


    // Criar médico
    @PostMapping
    public ResponseEntity<?> criarMedico(@Valid @RequestBody MedicoDTO medicoDTO) { // +++ Usa DTO
        try {
            // O Service agora cuida de codificar senha e salvar horários
            Medico medico = medicoService.criarMedico(medicoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(medico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<List<Medico>> listarTodos() { // +++ Retorna ResponseEntity
        List<Medico> medicos = medicoService.listarTodos();
        return ResponseEntity.ok(medicos);
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Medico> buscarPorId(@PathVariable Long id) { // +++ Nome do método e Retorno atualizados
        return medicoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarMedico(@PathVariable Long id,
                                             @Valid @RequestBody MedicoDTO medicoDTO) { // +++ Usa DTO
        try {
            // O Service agora cuida de limpar horários antigos e salvar os novos
            Medico medico = medicoService.atualizarMedico(id, medicoDTO);
            return ResponseEntity.ok(medico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Excluir
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMedico(@PathVariable Long id) { // +++ Nome do método e Retorno atualizados
        try {
            medicoService.deletarMedico(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}