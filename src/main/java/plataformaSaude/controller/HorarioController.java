package plataformaSaude.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.model.Horario;
import plataformaSaude.repository.HorarioRepository;

import java.util.List;

@RestController
@RequestMapping("/horarios")
public class HorarioController {

    @Autowired
    private HorarioRepository horarioRepository;

    @PostMapping
    public Horario criar(@RequestBody Horario horario) {
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
        return horarioRepository.save(existente);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        horarioRepository.deleteById(id);
    }
}


//Spring Boot, os endpoints fica assim:
//
//POST /horarios → cria um horário
//
//GET /horarios → lista todos
//
//GET /horarios/{id} → busca por ID
//
//PUT /horarios/{id} → atualiza
//
//DELETE /horarios/{id} → remove