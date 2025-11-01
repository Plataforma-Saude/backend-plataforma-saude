package plataformaSaude.controller;

import org.springframework.web.bind.annotation.*;
import plataformaSaude.model.Paciente;
import plataformaSaude.repository.PacienteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import plataformaSaude.dto.PacientePerfilDTO;
import plataformaSaude.service.UsuarioService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class PacienteController {

    private final PacienteRepository repository;
    private final UsuarioService usuarioService;

    public PacienteController(PacienteRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
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


    /**
     * Endpoint para o PACIENTE LOGADO buscar o PRÓPRIO perfil.
     */
    @GetMapping("/perfil")
    public Paciente buscarPerfil(Authentication authentication) {
        // 1. Descobrir quem está logado
        String email = authentication.getName();

        // 2. Encontrar o paciente no banco e retornar
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Paciente não encontrado"
                ));
    }

    /**
     * Endpoint para o PACIENTE LOGADO atualizar o PRÓPRIO perfil.
     */
    @PutMapping("/perfil")
    public Paciente atualizarPerfil(
            @RequestBody PacientePerfilDTO dto,
            Authentication authentication // <-- Spring injeta o usuário logado aqui
    ) {
        // 1. Descobrir quem está logado
        // O "username" (que definimos como o email no login) vem daqui
        String email = authentication.getName();

        // 2. Encontrar o paciente no banco
        Paciente paciente = repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Paciente não encontrado"
                ));

        // 3. Atualizar os dados (só os que vieram)
        if (dto.getNomeCompleto() != null) {
            paciente.setNomeCompleto(dto.getNomeCompleto());
        }
        if (dto.getCpf() != null) {
            paciente.setCpf(dto.getCpf());
        }
        if (dto.getEmail() != null) {
            paciente.setEmail(dto.getEmail());
        }
        if (dto.getCelular() != null) {
            paciente.setCelular(dto.getCelular());
        }
        if (dto.getProfissao() != null) {
            paciente.setProfissao(dto.getProfissao());
        }
        if (dto.getDataNascimento() != null) {
            paciente.setDataNascimento(dto.getDataNascimento());
        }
        if (dto.getCep() != null) {
            paciente.setCep(dto.getCep());
        }
        if (dto.getCidade() != null) {
            paciente.setCidade(dto.getCidade());
        }
        if (dto.getRua() != null) {
            paciente.setRua(dto.getRua());
        }
        if (dto.getNumero() != null) {
            paciente.setNumero(dto.getNumero());
        }
        if (dto.getComplemento() != null) {
            paciente.setComplemento(dto.getComplemento());
        }
        if (dto.getEstado() != null) {
            paciente.setEstado(dto.getEstado());
        }
        if (dto.getNotificacoes() != null) {
            // Assumindo que seu Paciente/Usuario tem um setNotificacoes(Boolean notificacoes)
            // paciente.setNotificacoes(dto.getNotificacoes());
        }
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            // Criptografa a nova senha antes de salvar
            paciente.setSenha(usuarioService.hashSenha(dto.getSenha()));
        }

        // 4. Salvar e retornar
        return repository.save(paciente);
    }

    // Excluir
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
