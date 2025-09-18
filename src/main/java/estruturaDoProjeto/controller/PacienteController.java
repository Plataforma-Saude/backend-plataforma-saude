package estruturaDoProjeto.controller;

import org.springframework.web.bind.annotation.*;
import estruturaDoProjeto.Paciente;
import estruturaDoProjeto.repository.PacienteRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class PacienteController {

    private final PacienteRepository repository;

    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    // Criar paciente
    @PostMapping
    public Paciente criar(@RequestBody Paciente paciente) {
        return repository.save(paciente);
    }

    // Listar todos
    @GetMapping
    public List<Paciente> listarTodos() {
        return repository.findAll();
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public Optional<Paciente> buscar(@PathVariable Long id) {
        return repository.findById(id);
    }

    // Atualizar
    @PutMapping("/{id}")
    public Paciente atualizar(@PathVariable Long id, @RequestBody Paciente novosDados) {
        return repository.findById(id)
                .map(p -> {
                    p.setNome(novosDados.getNome());
                    p.setSobrenome(novosDados.getSobrenome());
                    p.setEmail(novosDados.getEmail());
                    p.setProfissao(novosDados.getProfissao());
                    return repository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    // Excluir
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
