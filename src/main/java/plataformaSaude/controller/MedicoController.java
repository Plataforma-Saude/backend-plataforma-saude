package plataformaSaude.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.model.Medico;
import plataformaSaude.repository.MedicoRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoRepository repository;

    public MedicoController(MedicoRepository repository) {
        this.repository = repository;
    }

    // Criar médico

    @PostMapping
    public Medico criar(@RequestBody Medico medico) {
        return repository.save(medico);
    }
   
    // Listar todos
    @GetMapping
    public List<Medico> listarTodos() {
        return repository.findAll();
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public Optional<Medico> buscar(@PathVariable Long id) {
        return repository.findById(id);
    }

    // Atualizar
    @PutMapping("/{id}")
    public Medico atualizar(@PathVariable Long id, @RequestBody Medico novosDados) {
        return repository.findById(id)
                .map(m -> {
                    m.setNome(novosDados.getNome());
                    m.setSobrenome(novosDados.getSobrenome());
                    m.setEmail(novosDados.getEmail());
                    m.setCrm(novosDados.getCrm());
                    m.setEspecialidade(novosDados.getEspecialidade());
                    m.setFoto(novosDados.getFoto());
                    return repository.save(m);
                })
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
    }

    // Excluir
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
