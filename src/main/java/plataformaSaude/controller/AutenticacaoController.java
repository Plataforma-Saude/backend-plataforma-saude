package plataformaSaude.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.dto.PacienteRegistroDTO;
import plataformaSaude.model.Medico;
import plataformaSaude.model.Paciente;
import plataformaSaude.model.Usuario;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;
import plataformaSaude.service.UsuarioService;
import plataformaSaude.dto.PasswordResetRequest;
import plataformaSaude.dto.PasswordResetConfirm;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AutenticacaoController {

    @Autowired
    private UsuarioService usuarioService;

//    @Autowired
//    private MedicoRepository medicoRepository;
//
//    @Autowired
//    private PacienteRepository pacienteRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registrarPaciente(@Valid @RequestBody PacienteRegistroDTO dto) {
        if (usuarioService.buscarPorEmail(dto.getEmail()) != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erro: O email informado já está em uso");
        }

        if (usuarioService.buscarPorCpf(dto.getCpf()) != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erro: O CPF informado já está cadastrado.");
        }

        Paciente novoPaciente = new Paciente();
        novoPaciente.setEmail(dto.getEmail());
        novoPaciente.setCpf(dto.getCpf());

        // Vamos dividir o "nomeCompleto" em "nome" e "sobrenome"
        String nomeCompleto = dto.getNomeCompleto();
        String[] partesNome = nomeCompleto.trim().split(" ", 2); // Divide no primeiro espaço

        novoPaciente.setNome(partesNome[0]);

        if (partesNome.length > 1) {
            novoPaciente.setSobrenome(partesNome[1]);
        } else {
            novoPaciente.setSobrenome(""); // Deixa sobrenome vazio se só tiver um nome
        }

        novoPaciente.setSenha(dto.getSenha());

        try {
            usuarioService.salvarUsuario(novoPaciente);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Usuário registrado com sucesso");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(STR."Erro ao registrar usuário: \{e.getMessage()}");
        }
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<String> solicitarResetSenha(@RequestBody PasswordResetRequest request) {
        String email = request.getEmail();
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("O e-mail é obrigatório.");
        }

        return ResponseEntity.ok("Caso o seu e-mail esteja cadastrado na nossa plataforma, você receberá um e-mail em instantes.");
    }

    @PostMapping("/reset-senha")
    public ResponseEntity<String> redefinirSenha(@RequestBody PasswordResetConfirm request) {
        String token = request.getToken();
        String novaSenha = request.getNovaSenha();

        if (token == null || token.isEmpty() || novaSenha == null || novaSenha.isEmpty()) {
            return ResponseEntity.badRequest().body("Token e nova senha são obrigatórios.");
        }
        if (usuarioService.redefinirSenha(token, novaSenha)) {
            return ResponseEntity.ok("Nova senha criada com sucesso!!");
        } else {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }
    }
//    @PostMapping("/register/paciente")
//    public ResponseEntity<Paciente> registrarPaciente(@RequestBody Paciente paciente) {
//        paciente.setSenha(usuarioService.hashSenha(paciente.getSenha()));
//        Paciente novoPaciente = pacienteRepository.save(paciente);
//        return ResponseEntity.ok(novoPaciente);
//    }
//    @PostMapping("/register/medico")
//    public ResponseEntity<Medico> registrarMedico(@RequestBody Medico medico) {
//        medico.setSenha(usuarioService.hashSenha(medico.getSenha()));
//        Medico novoMedico = medicoRepository.save(medico);
//        return ResponseEntity.ok(novoMedico);
//    }
}